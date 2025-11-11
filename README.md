# heliosx-genovia-consultation-api

This project implements the backend service for the initial Consultation Phase for MedExpress for the Genovian Pear Allergy Medication.

## Tech Notes
This solution was developed using **Java 17** and **Spring Boot (v3.5.7)**.

## Instructions to Run the SPRING Web API Locally

### Prerequisites
1. JDK 17 or higher
2. Maven

### Steps tp run the Service

1. Start the application directly from Command Line Terminal or inside an IDE (preferrably IntelliJ)  or using Maven through the below given command:
```
./mvnw spring-boot:run
```

2. The application will start on port `8080`.

## Application Exposes 2 Endpoints:
### Endpoint 1 : GET http://localhost:8080/api/consultation/questions
```
curl -X GET http://localhost:8080/api/consultation/questions
```
Expected Response:
```
[
    {
    "id": "Q1",
    "text": "Have you ever had an adverse reaction to similar medication?",
    "type": "radio",
    "options": ["Yes", "No"]
    },
    // ... other questions (Q2, Q3, Q4)
]
```
### Endpoint 2 : POST http://localhost:8080/api/consultation/answers
```
curl -X POST http://localhost:8080/api/consultation/answers \
-H "Content-Type: application/json" \
-d '[
   {"questionId": "Q1", "value": "No"},
   {"questionId": "Q2", "value": "4"},
   {"questionId": "Q3", "value": "Yes"},
   {"questionId": "Q4", "value": "None"}
]
```
      
Expected Response: 
```
{
   "eligible": true,
   "doctorNotified": true,
   "message": "Prescription Under Review"
}
```

## Key Trade-Offs:

### 1. Hardcoded In Memory Data (as specified)
The questions have been hardcoded as specified in the problem statement, this can be improved by using a database in the future. Even for the MVP, I could have defined these Questions as a setting in application.properties and injected them using @Value and @ConfigurationProperties.

### 2. No Considerations for Deployments as we lack NFRs
We can keep separate settings for deployments, clusterization and load balancing using multiple properties files for dev/test/staging or prod profiles to scale independently, this widely depends on the Non-Functional Requirements which includes the RPMs expected as well. 

### 3. User Authentication/Authorisation
There is no user based authentication in this service at the moment, or any kind of role based authorisation which might be a security risk.

### 4. Lack of API Documentation
This service will benefit by creating Swagger or similar documentation, so that the consumers can use it more efficiently.

### 5. Data Validation
There is very minimal input validation in the project, this can be extended as per the API Documentation using Bean Validation.

### 6. Error Classification
Instead of raising Server Side Errors, a concise list of Error Classifications can be made which can help frontEnd to identify errors by looking at some kind of central database. This is for when the list of questions increases to a certain level.

### 7. Reducing Branching in Decision-Making while processing Answers (enhances Extensibility and Code Readability, reduces deep branching)
Currently, a lot of if and else statements are being used in processAnswers method which can be improved by using dedicated methods for multiple rules using list of predicates and lambdas and possibly using Strategy Design Pattern if there are multiple type of scenarios which need to be taken care of. 

### 8. Only Considers English language at the moment
A dedicated translating library or  the record can be extended to store the questions in multiple languages.

### 9. Request/Response Body
A more extensive request/response body can be created based on the requirement more extensively and probably discussing more functional requirements with stakeholders. 
