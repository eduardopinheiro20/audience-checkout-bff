# Audience Checkout BFF

Backend for Frontend for the Audience Checkout technical challenge.

## Purpose

This repository contains the BFF that will serve a subscription checkout flow.

The project starts with a small Clean Architecture / Ports & Adapters structure so that:
- HTTP concerns stay at the API boundary;
- checkout rules stay in the application/domain layers;
- external product and payment-method sources are represented by ports;
- mock implementations can later be replaced by HTTP clients without changing business rules.

## Stack

- Java 25
- Spring Boot 4.1.2
- Maven
- Spring Web MVC
- Jakarta Bean Validation
- JUnit 5 / Mockito
- Springdoc OpenAPI

## Initial architecture

```text
com.audiencecheckout.bff
├── api
│   ├── controller
│   └── dto
│       ├── request
│       ├── response
│       └── error
├── application
│   └── service
├── domain
│   ├── model
│   └── port
│       └── out
├── infrastructure
│   ├── client
│   │   └── mock
│   └── repository
│       └── memory
└── config
```

## Why this structure?

The challenge explicitly separates:
1. the BFF contract;
2. external product/payment-method services;
3. checkout rules.

The folder structure reflects those responsibilities and keeps dependencies pointing inward.

## Run locally

Environment setup will be done in the next step.

```bash
./mvnw spring-boot:run
```

or:

```bash
mvn spring-boot:run
```

The API will run on `http://localhost:8080`.

## Initial commit suggestion

```text
chore: initialize audience checkout BFF structure
```
