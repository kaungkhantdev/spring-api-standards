# Spring API Standards

The standards every Spring Boot API project follows: one response format for the whole API, one way to handle errors, and one folder structure. Each part has a guide and Java files ready to copy into a project.

Built and tested against Spring Boot 4.1.1 (Spring Framework 7, Java 21).

| Folder | What's in it |
|---|---|
| [api-response](api-response/README.md) | The `ApiResponse` envelope (`success`, `data`, `error`, `meta`) for every response, status codes, paging, JSON conventions, a mapper, and a complete example `order` feature with Swagger annotations |
| [enum-handling](enum-handling/README.md) | `DbEnum`, `DbEnumConverter` and `WebConfig`, so enums use one lowercase value (`dine_in`) in JSON, URLs and the database |
| [error-handling](error-handling/README.md) | `BusinessException`, `GlobalExceptionHandler` and `SecurityConfig`, so every error comes back in the same envelope with the right HTTP status; with flow diagrams |
| [project-structure](project-structure/README.md) | How the code is organised: feature folders, layers, enums, tests and rules |

## Folder layout

```
spring-api-standards/
├── README.md                ← this page
├── api-response/
│   ├── README.md
│   ├── common/              ← ApiResponse, ApiError, Meta, Pagination
│   ├── order/               ← example feature: controller, service, entity, enums, dto, mapper
│   └── examples/            ← real JSON for every request and response
├── enum-handling/
│   ├── README.md
│   └── common/              ← DbEnum, DbEnumConverter, WebConfig
├── error-handling/
│   ├── README.md
│   ├── common/              ← exception base classes, GlobalExceptionHandler, SecurityConfig
│   ├── auth/, order/        ← example feature exceptions
│   └── images/              ← flow diagrams (.mmd source and .png)
└── project-structure/
    ├── README.md
    └── images/
```

## Using it in a project

1. **Copy the `common/` Java files** from `api-response/`, `error-handling/` and `enum-handling/` into your project's `src/main/java/`, keeping the package paths. They depend on each other, so copy all three.
2. **Change the base package.** The files use `com.astronity.pos`; replace it with your project's base package, e.g. `com.astronity.inventory`. In IntelliJ: right-click the package → **Refactor → Rename**.
3. **Add the validation starter** to `pom.xml`, so `@Valid` errors are reported:

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-validation</artifactId>
   </dependency>
   ```

4. **Follow the `order/` folders as the pattern** for each feature: throw a feature exception from the service, return `ApiResponse.ok(...)` from the controller.
5. **Start the app.** `GlobalExceptionHandler` and `SecurityConfig` are picked up automatically.

The `examples/` and `images/` folders are documentation only; don't copy them into a project.

## About the examples

The examples come from the Astronity POS, the first project to use these standards: an `order` feature, `auth` exceptions, and the POS's own [project structure](project-structure/README.md), whose links to the ERD and flowcharts point to `pos-docs`. In another project, keep the patterns and replace the POS features with your own.

## Changing the standards

A change here applies to every project, so:

- **Discuss it first** with the teams that use it.
- **Update the guide, the Java files and the examples together**, so they never disagree.
- **Tell each project what changed**, because projects copy the files and won't get the change on their own.
