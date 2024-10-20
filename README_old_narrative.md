
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

___

#### The Bank APIs
The Bank first sends a ```validation``` request to ascertain that a recipient for the payments actually exists in the XYZ system.

XYZ will return a simple __```Student```__ object if the student specified by Student ID exists. If not, a ```404``` response will be issued.

> There is room to improve the reponse for validation. There could be preference to return an __```IsValid```__ which looks like
```json
{
	"student_id": "BE232",
	"is_valid": "true"
}
```
> The kind of response should be pronounced by the Bank and the client to conform, based on a product line (ex. _Shool Fee Programme_)


When it's valid, the Bank API proceeds to queue the Payment notification. To address the challenge of integration:
1. __Security__
The client should whitelist only IPs of the Bank that should communicate to the Student Validation and Payment Notification endpoints. 
That's because the bank so many clients it communicates to and it's infeasible to maintain a process that validates their authenticity.

The clients can be offered a __Dashboard__ (API) to specify their Student Validation endpoints and Payment Notification endpoints.

2. __Seamless Integration__ solution
Maintain a repository that stores information going out to the clients.
> 
```sql
payment_notifications = { NOTIFICATION_ID + CLIENT_ID + PAYMENT_DETAILS + QUEUED_AT + IS_SENT + SENT_AT + IS_ACKNOWLEDGED + ACKNOWLEDGEMENT_RESPONSE + SEND_COUNT + COMMENT_LOG }
```

Using above structure, we can monitor when a notification is sent to the client, and if it's received. 

This should be the kind of response a client should send as `acknowledgement`:
```json
{
	"notification_id": "abc321-th7g5p"
	"status": "received"
}
```


#### Challenges to Brainstorm:
> Should the notification be synchronous? 
When a payment notification is received: 

* should the message be just queued for saving to DB before sending to the client (XYZ API),
 
or 

* should we publish two Events (```SAVE_TO_DB```, ```SEND_TO_CLIENT```)

> The first approach implies that in case of a database failure, the client will not receive the notification until the Bank's database is recovered. 
_What does that mean for a student waiting for the payment to reflect?_

> The second approach seems seems optmistic, efficient due to it's non-waiting, but a careful though-process will be held and documentated to discover any implications 
and contrast it to the first approach. For now, we work with the first one, then we can adjust it to become the second. 
