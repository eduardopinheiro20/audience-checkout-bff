# Audience Checkout BFF

Backend for Frontend (BFF) developed for the Audience Checkout technical challenge.

This service orchestrates the subscription checkout flow between the frontend and simulated external services for products and payment methods.

Repository: https://github.com/eduardopinheiro20/audience-checkout-bff.git

---

## Purpose

The project implements:

- pre-checkout creation;
- product validation;
- available payment methods;
- credit card, debit card and Pix checkout;
- simulated payment approval and decline;
- PostgreSQL cart persistence;
- stable error contracts for the frontend;
- automated tests.

The project follows a small Ports & Adapters / Clean Architecture approach so that:

- HTTP concerns stay at the API boundary;
- checkout rules remain in the application/domain layers;
- product and payment-method sources are accessed through ports;
- infrastructure implementations can be replaced without changing business rules.

---

## Stack

- Java 25
- Spring Boot 4.1.2
- Spring Web / Spring MVC
- Spring Data JPA
- PostgreSQL
- H2 for automated tests
- Maven
- Docker / Docker Compose
- JUnit 5
- Mockito
- JaCoCo
- Swagger / OpenAPI

---

## Project structure

```text
src/main/java/com/audiencecheckout/bff
├── api
│   ├── controller
│   ├── dto
│   │   ├── request
│   │   ├── response
│   │   └── error
│   └── error
├── application
│   ├── exception
│   └── service
├── domain
│   ├── model
│   └── port
│       └── out
├── infrastructure
│   ├── client
│   │   └── mock
│   └── persistence
│       ├── adapter
│       ├── entity
│       └── repository
└── config
```

Main responsibilities:

- `api`: HTTP controllers, DTOs and error handling;
- `application`: checkout use cases and business flow;
- `domain`: core models and ports;
- `infrastructure`: persistence and simulated external service adapters;
- `config`: application configuration.

---

## Requirements

Install:

- Java 25+
- Maven 3.9+
- Git
- Docker with Docker Compose

On Windows, Docker Desktop is recommended.

Check the installations:

```bash
java -version
mvn -version
git --version
docker --version
docker compose version
```

---

## Clone the repository

### Linux / macOS

```bash
git clone https://github.com/eduardopinheiro20/audience-checkout-bff.git
cd audience-checkout-bff
```

### Windows PowerShell

```powershell
git clone https://github.com/eduardopinheiro20/audience-checkout-bff.git
cd audience-checkout-bff
```

---

## Start PostgreSQL

Make sure Docker is running.

```bash
docker compose up -d
```

This command is the same on Linux, macOS and Windows PowerShell.

Check the container:

```bash
docker ps
```

Expected container name:

```text
audience-checkout-postgres
```

---

## Database configuration

```text
Host: localhost
Port: 5432
Database: checkout_db
Username: checkout
Password: checkout
```

These values are already configured for local development.

---

## Run the API

After PostgreSQL is running:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

## Swagger

Open:

```text
http://localhost:8080/swagger-ui.html
```

Swagger can be used to test the complete backend flow without running the frontend.

---

## Checkout flow

Main endpoints:

```text
POST /operations/pre-checkout
POST /operations/checkout
```

Flow:

```text
Pre-checkout
    ↓
Cart creation
    ↓
Choose payment method
    ↓
Checkout
    ↓
Approved or declined
```

---

## 1. Pre-checkout

Endpoint:

```http
POST /operations/pre-checkout
```

Request:

```json
{
  "products": [
    {
      "slug": "premium"
    }
  ]
}
```

Example response:

```json
{
  "cartId": "f2c63742-d635-4c95-9c9d-80a358e70793",
  "products": [
    {
      "slug": "premium",
      "name": "Premium Plan",
      "description": "Complete monthly subscription",
      "price": 39.90,
      "currency": "BRL"
    }
  ],
  "paymentMethods": [
    { "name": "Credit Card" },
    { "name": "Debit Card" },
    { "name": "Pix" }
  ]
}
```

Copy the returned `cartId`. It is required for checkout.

---

## 2. Successful card checkout

Endpoint:

```http
POST /operations/checkout
```

Request:

```json
{
  "cartId": "PASTE_CART_ID_HERE",
  "paymentMethod": {
    "name": "Credit Card",
    "card": {
      "holderName": "Ana Souza",
      "number": "4111111111114242",
      "expiry": "12/28",
      "cvv": "123"
    }
  }
}
```

Expected response:

```text
HTTP 200 OK
```

Example:

```json
{
  "cartId": "f2c63742-d635-4c95-9c9d-80a358e70793",
  "status": "APPROVED",
  "paidAt": "2026-10-29T18:00:00Z",
  "payment": {
    "method": "Credit Card",
    "lastDigits": "4242",
    "pixCode": null,
    "amount": 39.90,
    "currency": "BRL"
  },
  "products": [
    {
      "slug": "premium",
      "name": "Premium Plan",
      "price": 39.90
    }
  ]
}
```

The full card number and CVV are never persisted. Only the last four digits are returned.

---

## 3. Simulating a declined payment

Any card ending in `0002` is intentionally declined.

Create a new cart with pre-checkout and then use:

```json
{
  "cartId": "PASTE_CART_ID_HERE",
  "paymentMethod": {
    "name": "Credit Card",
    "card": {
      "holderName": "Ana Souza",
      "number": "4111111111110002",
      "expiry": "12/28",
      "cvv": "123"
    }
  }
}
```

Expected response:

```text
HTTP 422 Unprocessable Entity
```

Example:

```json
{
  "displayType": "BLOCK",
  "errorCode": "PAYMENT_DECLINED",
  "cartId": "f2c63742-d635-4c95-9c9d-80a358e70793",
  "frontendError": {
    "code": "P-1001",
    "title": "Payment not approved",
    "description": "The payment could not be completed. Try again or choose another payment method.",
    "primaryButton": {
      "label": "Try again",
      "url": "RETRY"
    },
    "secondaryButton": {
      "label": "Change payment method",
      "url": "/checkout/payment"
    }
  }
}
```

This behavior exists specifically to demonstrate the frontend error flow.

---

## 4. Pix checkout

Pix does not send card data.

```json
{
  "cartId": "PASTE_CART_ID_HERE",
  "paymentMethod": {
    "name": "Pix",
    "card": null
  }
}
```

Expected:

```text
HTTP 200 OK
```

The API returns a simulated Pix code. No real payment gateway is integrated.

---

## Business rules

- Checkout only works for a `cartId` created by pre-checkout.
- The selected payment method must belong to the cart.
- Invalid products are rejected during pre-checkout.
- Cards ending in `0002` are intentionally declined.
- An approved cart cannot be paid again.
- Full card number and CVV are never persisted.
- Only the last four card digits are returned after successful card payment.
- Pix checkout does not require card information.

---

## Error contract

All business errors returned to the frontend use a stable structure.

Main scenarios:

| Scenario | HTTP Status | Error Code |
|---|---:|---|
| Product not found | 404 | `PRODUCT_NOT_FOUND` |
| Cart not found | 404 | `CART_NOT_FOUND` |
| Payment method not allowed | 400 | `PAYMENT_METHOD_NOT_ALLOWED` |
| Payment declined | 422 | `PAYMENT_DECLINED` |
| Cart already approved | 409 | `CART_ALREADY_APPROVED` |

---

## Automated tests

Run all tests:

```bash
mvn clean test
```

This command works on Linux, macOS and Windows PowerShell when Maven is installed.

PostgreSQL does not need to be running for the tests. The test profile uses H2 in memory.

The suite covers:

- application context startup;
- successful pre-checkout;
- invalid product;
- successful credit card checkout;
- successful Pix checkout;
- declined payment;
- cart not found;
- payment method not allowed;
- duplicate payment;
- REST controller contracts;
- global error handling.

Current result:

```text
Tests run: 17
Failures: 0
Errors: 0
Skipped: 0
```

---

## Test coverage

JaCoCo is configured in the project.

Generate the report with:

```bash
mvn clean test
```

Report:

```text
target/site/jacoco/index.html
```

Current overall coverage is approximately 72%.

Main business areas have higher coverage:

```text
CheckoutApplicationService: 98%
GlobalExceptionHandler: 100%
```

The test strategy prioritizes business rules, API contracts and error scenarios instead of artificially maximizing coverage for persistence-only classes.

---

## External services

Product and payment-method services are simulated through ports:

```text
ProductCatalogPort
PaymentMethodPort
```

Their mock implementations are located in the infrastructure layer.

A real HTTP client can replace these adapters without changing the checkout business rules.

---

## Persistence

PostgreSQL persists checkout carts.

Persistence is isolated behind:

```text
CartRepositoryPort
```

The application layer does not depend directly on Spring Data JPA.

---

## Security considerations

Sensitive payment data is only used during the checkout request.

The application does not persist:

```text
Full card number
CVV
```

Successful card payments return only:

```json
{
  "lastDigits": "4242"
}
```

---

## Design decisions

Implemented:

- Spring Boot
- PostgreSQL
- Docker
- Swagger/OpenAPI
- JUnit
- Mockito
- H2 for tests
- JaCoCo
- Ports & Adapters separation

Not implemented:

- real payment gateway
- RabbitMQ
- Kafka
- Redis

These items were not required for the main checkout flow and would add unnecessary infrastructure for this challenge.

The priority was to keep the solution clear, testable, predictable and easy to run.

---

## Stop PostgreSQL

Stop the database:

```bash
docker compose down
```

Remove the database volume as well:

```bash
docker compose down -v
```

Use `-v` only when you intentionally want to delete local database data.

---

## Quick start

```bash
git clone https://github.com/eduardopinheiro20/audience-checkout-bff.git
cd audience-checkout-bff
docker compose up -d
mvn spring-boot:run
```

Open:

```text
http://localhost:8080/swagger-ui.html
```

Run tests:

```bash
mvn clean test
```

Simulation cards:

```text
Successful:
4111111111114242

Declined:
4111111111110002
```

---

## Author

Eduardo Pinheiro
