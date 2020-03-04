# Spring REST with Spring Security + Swagger + Docker


## 1. How to start

$ git clone https://github.com/rdangi/spring-rest-demo.git

## Maven run

$ mvn spring-boot:run

## Docker build and run

$ docker build . -t spring-rest-demo

$ docker run --publish=8080:8080 spring-rest-demo


## Access REST API

http://localhost:8080/products

user/password,
admin/password

It can also be accessed through any REST client, easiest is Postman can be downloaded from here: https://www.postman.com/downloads/

## Swagger
http://localhost:8080/swagger-ui.html#/
