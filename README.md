#### XYZ Integration

Featuring microservice architecture, this project demonstrates how integration between disparate systems can be made seamless. 

The project is based on Spring Boot 3.3, features application messaging (using __Rabbit__) to provide resilience againsts failures, caching (using __Redis__) 
to improve performance and reduce database walks for static data, and database migration (using __Flyway__) to facilitate database staging.

___


#### The Architecture
![View Architecture Design](./doc/Structure_flow.webp)

Going with the requirement, the client end will be developed, then have a mock (basic) API to represent the Bank sending ``validation`` and ``payment notification`` to XYZ Gateway API

#### Prerequisites
Only __Docker__ with ``Docker Swarm enabled``


#### Quickstart
To jump into action. 
1. Set up the project:
Clone from repo and build the project

```sh
git clone https://github.com/david-matu/xyz_integration_demo.git

# Now buid the project with Gradle abd Start the landscape with Docker Swarm (Compose)
cd xyz_integration_demo

./gradlew build && docker compose build && docker compose up -d
```

2. Download this [API collection](./XYZ-Integrations.postman_collection.json) and test. Open with Postman or your favourite API client tool

or

Use the following request details for executions and responses as reference:
##### Validate Student
__url:__ ```http://localhost:9090/bank/integration/validate-student```

__Payload__

```json
{
    "request_id": "123-10292-bbg28njag-fah-1284",
    "payment_ref": "REF-123-FROM-SYS-INTERNAL-2024",
    "institution_id": "xyz-university",
    "payment_details": {
        "student_id": "EG23",
        "account_number": "10101"
    }
}
```

__Expected Responses__

___Enrolled Student (Valid account)___
```json
{
    "responseStatus": "ENROLLED",
    "responseMessage": "Student David with id BE232 is valid"
}
```


___Enrolled Student (Invalid account)___
```json
{
    "responseStatus": "ENROLLED_INVALID_ACCOUNT_NUMBER",
    "responseMessage": "Student (David) with id BE232 is not eligible for specified account (10101)"
}
```

___Student not Enrolled___
```json
{
    "responseStatus": "NOT_ELIGIBLE",
    "responseMessage": "Student not eligible for tuition payments"
}
```

Once the client confirms that the Student ID and account number for which the payment is to be made are valid, the transaction is then posted. Use the following details:

__URL:__ http://localhost:9090/bank/integration/send-payment-notification

__Payload__
```json
{
    "institution_id": "xyz-university",
    "callback_url": "http://localhost:8080/bank-channel/callback-demo",
    "payment_details": {
        "payment_ref": "REF-123-FROM-SYS-INTERNAL-2024-2",
        "student_id": "BE232",
        "first_name": "David",
        "last_name": "Matu",
        "account_number": "10100",
        "amount_paid": "12000.0",
        "currency": "KES",
        "date_paid": "2024-10-22T08:02:18.851Z",
        "branch": "02"
    }
}
```

__Expected responses:__
___Accepted___

This means that the client (XYZ Gateway API) has received the payment notification. The processing is now on the client side.

```json
{
    "responseStatus": "ACCEPTED",
    "responseMessage": "Payment notification received by client for Student ID: BE232 and Payment reference: REF-123-FROM-SYS-INTERNAL-2024-2"
}
```

In the event the client's gateway is down:
```sh
docker compose down xyz-gateway-service
```

```json
{
    "responseStatus": "Server Error",
    "responseMessage": "There was an error posting payment notification to client: Failed to resolve 'xyz-gateway-service' [A(1)]"
}
```

This message will be moved to a dead letter queue after several trials (can be configured), for auditing purposes.


In the event that the client gateway is up but the core microservice receiving this request (xyz-payment-service) is down or faulty, 
the message will be retried and if the error persists, the message will be moved to a dead letter queue. Since this request is asynchronous, 
we'l get the following response anyways:

```json
{
    "responseStatus": "ACCEPTED",
    "responseMessage": "Payment notification received by client for Student ID: BE232 and Payment reference: REF-123-FROM-SYS-INTERNAL-2024-2"
}
```


See queued messages on Rabbit MQ:
[http://localhost:15672/#/](http://localhost:15672)

Log in as ```guest``` / ```guest```



#### Requirement Gathering, Thought-process and Design
> The old narrative has been moved to file [README_old_narrative.md](./README_old_narrative.md) to keep this page concise.


#### Data Strucures

Database Structures:

1. XYZ University Services 
These support the core APis for XYZ University


* __``enrolment``__

Facilitates Student validation to aid verdict on posting payment notification 


```sql
+----------------+--------------+------+-----+---------+-------+
| Field          | Type         | Null | Key | Default | Extra |
+----------------+--------------+------+-----+---------+-------+
| STUDENT_ID     | varchar(200) | NO   | PRI | NULL    |       |
| FIRST_NAME     | varchar(25)  | YES  |     | NULL    |       |
| LAST_NAME      | varchar(45)  | YES  |     | NULL    |       |
| ACCOUNT_NUMBER | varchar(200) | YES  |     | NULL    |       |
| STATUS         | varchar(45)  | YES  |     | NULL    |       |
+----------------+--------------+------+-----+---------+-------+
```

* __``payments``__

Persists Payment notifications from the bank

```sql
+--------------------+--------------+------+-----+---------+-------+
| Field              | Type         | Null | Key | Default | Extra |
+--------------------+--------------+------+-----+---------+-------+
| PAYMENT_ID         | varchar(255) | NO   | PRI | NULL    |       |
| EXTERNAL_REFERENCE | varchar(255) | YES  |     | NULL    |       |
| FOR_INVOICE_ID     | varchar(50)  | YES  |     | NULL    |       |
| STUDENT_ID         | varchar(50)  | YES  |     | NULL    |       |
| AMOUNT_PAID        | double       | YES  |     | NULL    |       |
| DATE_PAID          | datetime     | YES  |     | NULL    |       |
| WALLET             | varchar(50)  | YES  |     | NULL    |       |
| COMMENT            | text         | YES  |     | NULL    |       |
+--------------------+--------------+------+-----+---------+-------+
```


2. Banking Services
* __bank_clients__
```sql
+-------------------------+--------------+------+-----+---------+-------+
| Field                   | Type         | Null | Key | Default | Extra |
+-------------------------+--------------+------+-----+---------+-------+
| CLIENT_ID               | varchar(255) | NO   | PRI | NULL    |       |
| INSTITUTION_NAME        | varchar(255) | YES  |     | NULL    |       |
| VALIDATION_ENDPOINT     | varchar(255) | YES  |     | NULL    |       |
| PAYMENT_NOTIFICATION_EP | varchar(255) | YES  |     | NULL    |       |
+-------------------------+--------------+------+-----+---------+-------+
```


* __payment_notifications__
```sql
+--------------------------+--------------+------+-----+---------+-------+
| Field                    | Type         | Null | Key | Default | Extra |
+--------------------------+--------------+------+-----+---------+-------+
| NOTIFICATION_ID          | varchar(255) | NO   | PRI | NULL    |       |
| CLIENT_CALLBACK_URL      | varchar(255) | NO   |     | NULL    |       |
| CLIENT_ID                | varchar(255) | YES  |     | NULL    |       |
| PAYMENT_BODY             | json         | YES  |     | NULL    |       |
| QUEUED_AT                | datetime     | YES  |     | NULL    |       |
| IS_SENT                  | int          | YES  |     | NULL    |       |
| SENT_AT                  | datetime     | YES  |     | NULL    |       |
| IS_ACKNOWLEDGED          | int          | YES  |     | NULL    |       |
| ACKNOWLEDGEMENT_RESPONSE | text         | YES  |     | NULL    |       |
| SEND_COUNT               | int          | YES  |     | NULL    |       |
| COMMENT_LOG              | text         | YES  |     | NULL    |       |
+--------------------------+--------------+------+-----+---------+-------+
```


#### Services
All the services are bundled as a single project for simplicity and portability, capable of spinning up an entire landscape of services on __Docker__

Using Docker compose, the services can be brought up altogether.

* __Bank Services__
Sends _Student Validation_ and _Payment notification_ requests to the Gateway services of Xyz apis

 
* __Enrolment Service__  (xyz)
A core service that hosts student enrolment and serves _Student Validation_ to the Gateway API


* __Payment Service__    (xyz)
A core service that serves to persist payment notifications received at the gateway. This API consumes message from queue to save to database


* __Gateway Service__    (xyz)
Responsible for hiding the core services for Xyz such that only desired APIs can interact with the external clients. Security will be implemented here.

##### Structure and Flow of Data in the Services


#### Running the services
This is a Gradle-built project that runs on Docker swarm.
> No need to install Gradle, the project comes with a Gradle wrapper.


1. Fork the project 
```sh 
git clone https://github.com/david-matu/xyz_integration_demo.git

cd xyz_integration_demo

./gradlew build && docker compose build && docker compose up -d
```

You should see logs like these after successful startup:
```sh
✔ Container xyz_services_integrated-rabbitmq-1               Healthy                                                                                                                                                                   11.5s 
 ✔ Container xyz_services_integrated-bank_mysql-1             Healthy                                                                                                                                                                   12.3s 
 ✔ Container xyz_services_integrated-mysql-1                  Healthy                                                                                                                                                                   10.8s 
 ✔ Container xyz_services_integrated-xyz-payment-service-1    Started                                                                                                                                                                   11.0s 
 ✔ Container xyz_services_integrated-xyz-enrolment-service-1  Started                                                                                                                                                                   11.0s 
 ✔ Container xyz_services_integrated-xyz-gateway-service-1    Started                                                                                                                                                                   11.8s 
 ✔ Container xyz_services_integrated-bank-service-1 
 
```

#### API Documentation
After the services are up, access the documentation on the browser at:

[http://localhost:9000/openapi/swagger-ui.html](http://localhost:9000/openapi/swagger-ui.html)

For core services:

The core microservices should actually be hidden from the external networks so that only the gateway service is interacting with them. 

For this demo, they have been exposed for debugging and proofing purposes.

* __Bank Service__


[localhost:9090/openapi/swagger-ui.html](http://localhost:9090/openapi/swagger-ui.html)

* __xyz gateway service__


[http://localhost:9000/openapi/swagger-ui.html](http://localhost:9000/openapi/swagger-ui.html)

* __xyz enrolment service__


[http://localhost:9001/xyz-core/openapi/webjars/swagger-ui/index.html#/](http://localhost:9001/xyz-core/openapi/swagger-ui.index.html) 


* __xyz payment service__


[localhost:9002/openapi/swagger-ui.html](localhost:9002/openapi/swagger-ui.html)


#### Monitoring Events in Rabbit MQ
Navigate to the following link to monitor messages: [http://localhost:15672/](http://localhost:15672/)

Use ```guest``` as both username and password


#### Logs from Nodes:
In advanced stage of this project, the logs can be shipped to `ELK`. At the moment, for simplicity, we can monitor logs per node on the terminal like so:

In the project root directory:
```sh
# Append -f flag to display them as they happen 
docker compose logs xyz-enrolment-service -f

docker compose logs xyz-payment-service -f

docker compose logs bank-service -f

docker compose logs xyz-gateway-service

docker compose logs mysql

docker compose logs rabbit
```
> Take note that the names are as they were baptised in the `docker-compose-file`


#### Monitoring
Nodes can be best monitored by services such as ``Prometheus`` and ``Grafana``, to show their health statuses.

At the moment, for simplicity reasons, the apis are equipped with a healthcheck capability 
which is aggregated by a selected api (on gateway) and returned as a single object showing how each service is faring

#### Interacting with the Nodes: databases, Rabbit, microservices:

For each of the instances, ensure you are in the project's root directory (where ```docker-compose``` resides


__mysql__
```sh
docker compose exec mysql mysql -u david -p
docker compose exec bank_mysql mysql -u david -p

# Password will be prompted: enter david123
```  

