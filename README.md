# CustomersApp

Customers application is CRUD application. 

Provides mechanisms:

- [generated customers*](#generate-users)
- save customers to file


Web view with sample of generated data:
<img src="/images/view-welcome.JPG">

## Table of content

1. [Used Technology](#used-technology-and-lib)
1. [REST](#api-rest)
1. [Ways to generate data](#generate-users)
1. [Schema database](#database-schema)

## Used Technology

- Java 21
- JUnit 5
- Google truth 1.1
- Gradle
- Spring 
- Hibernate
- Thymeleaf
- Swagger


## API REST

| Method    | Endpoint |   Description  |
|-----------|----------|-------------|
| POST    | /customers    | add single user (name, surname, pesel) |
| POST | /customers/{peselNum}/methods     | add communication method to user |
| GET | /customers    | get all users |
| GET | /customers/{peselNum}    | get user by pesel |
| GET | /customers/export    | save all users to file |
| GET | http://localhost:8080/welcome   | Display all user on website|
| GET | http://localhost:8080/welcome/{pageNumber}     |Display all user on website|

<img src="/images/view-welcome.JPG">

## Generate users

Details: 
* generate users 
* 2-4 communication method (random for each one)

1. easiest way - gradle task: generateCustomersAndContacts </br>
generated 10 users
1. best way - use generator console project
</br>
https://github.com/Joanna-Szczesna/CustomersApp/tree/main/generator

* base on most popular names and surnames in Poland;
* default generates 10 people

Output - links + statuses adds method:
</br>
<img src="/images/customer_generator_output.JPG">

### How to use generator project?

1. run Customerapp
2. run generator project with string args 
* first int number will be number of generated people
* without args program generated 10 people by default
3. program use post method to add people with contact method


H2 view:
<img src="/images/h2_console.JPG">
<img src="/images/h2_console_methods.JPG">

## Database schema

<img src="/images/costumerDB_schema.png">


## TODO:
polish chars


