# Spring Boot Common

Shared backend building blocks for the Astronity POS Spring Boot API: one response format for the whole API, one way to handle errors, and the folder structure the backend follows. Each part has its own guide and Java files ready to copy into the backend.

Built and tested against Spring Boot 4.1.1 (Spring Framework 7, Java 21).

| Folder | What's in it |
|---|---|
| [api-response](api-response/README.md) | The `ApiResponse` envelope (`success`, `data`, `error`, `meta`) for every response, status codes, paging, JSON conventions, a mapper, and a complete example `order` feature with Swagger annotations |
| [error-handling](error-handling/README.md) | `BusinessException`, `GlobalExceptionHandler` and `SecurityConfig`, so every error comes back in the same envelope with the right HTTP status; with flow diagrams |
| [project-structure](project-structure/README.md) | How the backend and frontend code is organised: feature folders, layers, enums, tests and rules |

## Folder layout

```
spring-boot-common/
├── README.md                ← this page
├── api-response/
│   ├── README.md
│   ├── common/              ← ApiResponse, ApiError, Meta, Pagination, enum helpers, WebConfig
│   ├── order/               ← example feature: controller, service, entity, enums, dto, mapper
│   └── examples/            ← real JSON for every request and response
├── error-handling/
│   ├── README.md
│   ├── common/              ← exception base classes, GlobalExceptionHandler, SecurityConfig
│   ├── auth/, order/        ← example feature exceptions
│   └── images/              ← flow diagrams (.mmd source and .png)
└── project-structure/
    ├── README.md
    └── images/
```

## Using it in the backend

1. **Copy the `common/` Java files** from `api-response/` and `error-handling/` into `backend/src/main/java/com/astronity/pos/`, keeping the same package paths. They depend on each other, so copy both.
2. **Add the validation starter** to `pom.xml`, so `@Valid` errors are reported:

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-validation</artifactId>
   </dependency>
   ```

3. **Use the `order/` folders as the pattern** for each feature: throw a feature exception from the service, return `ApiResponse.ok(...)` from the controller.
4. **Start the app.** `GlobalExceptionHandler` and `SecurityConfig` are picked up automatically.

The `examples/` and `images/` folders are documentation only; don't copy them into the backend.

## Related

The database design, business flowcharts and migrations stay in [pos-docs](../pos-docs/README.md). Links from these guides to the ERD and flowcharts point there.
