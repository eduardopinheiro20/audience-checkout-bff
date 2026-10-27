# Backend Architecture

The backend follows a small Ports & Adapters approach.

```text
HTTP
 ↓
api/controller
 ↓
application/service
 ↓
domain
 ↓
domain/port/out
 ↓
infrastructure adapters
```

## Dependency direction

The domain and application layers do not depend on mock clients, storage technology, or HTTP.

External data sources are represented by interfaces:
- `ProductCatalogPort`
- `PaymentMethodPort`
- `CartRepositoryPort`

For the challenge, adapters are local mocks/in-memory implementations. They can later be replaced without changing the checkout rules.
