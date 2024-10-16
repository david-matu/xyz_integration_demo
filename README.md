#### XYZ Integration

Featuring microservice architecture, this project demonstrates how integration between disparate systems can be made seamless. 

The project is based on Spring Boot 3.3, features application messaging (using __Rabbit__) to provide resilience againsts failures, caching (using __Redis__) 
to improve performance and reduce database walks for static data, and database migration (using __Flyway__) to facilitate database staging.

___

##### XYZ Services

There are two key entities:
* __`active_students`__ - Student information schema
* __`payment_notifications`__ - 


##### Expected:
* Student Validation Endpoint
* Payment Notification Endpoint

##### Deduced Flow:
The Bank will send __payment_notification__ to the school's STUDENT_VALIDATION api (endpoint).

`STUDENT_VALIDATION api` should have cached student information for faster response


`PAYMENT_NOTIFICATION` api will receive payment information, store in a queue and acknowledge. It's a supplier

`PAYMENT_HANDLER` is a consumer api that will process the payment notification and proceed to update the records in the db: financial records against a student.


#### Security
For security of the communication:
The Bank API will have a repository to store client information such as the `payment validation url`, `payment notification url`, we can model it this way:

```sql
bank_clients = { CLIENT_ID + CLIENT_NAME + VALIDATION_URL + PAYMENT_NOTIFICATION_URL }

// This information can be fed into an identity provider so we have less burden of maintaining the clients.
// But, we can store this info in database and cache,

// Since the bank system is an external system that can't subscribe to the XYZ's apis, we enhance security of the enpoints with the following approach:
// To address security, on the XYZ University's systems, we can whitelist which IPs (the bank IP/ hostname) to allow for the payment validation and notifications
```

#### Recap:

The Bank will process payment for an institution like XYZ, such that the details of XYZ are probably pointed by an __account_number__ 

With the account number, we can fetch the entire details of the client such as VALIDATION and NOTIFICATION_URL.

According to the details given, that step is already done, let's take care of the client at XYZ universities side to handle validations and payment notification receptions.

This is a supossedly dedicated schema to store info regarding the client systems such as VALIDATION_ENDPOINTS.

Since the bank has a variety of clients, a client can be categorized in order to allow for different information. For instance, we can attach CATEGORY 
column to identify the kind of client (e.g. school, payment provider, e-commerce). Each of these would be served differently depending on the product offered by/ 
or consumed by the bank.

For now, we stick to the minimal logic of sending validation requests and payment notifications to an institution registered with the bank


#### Running the solution
This is a Gradle-built project that runs on Docker swarm

* Clone the repo
* Run the command:
```sh
./gradlew build && docker compose build && docker compose up -d
```


#### API Documentation
After the services are up, access the documentation on the browser at:

[http://localhost:9000/openapi/swagger-ui.html](http://localhost:9000/openapi/swagger-ui.html)  
