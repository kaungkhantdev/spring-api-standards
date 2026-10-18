# API Response

How the Astronity POS backend shapes every response. Success and error responses look the same, so the Angular app reads all of them the same way. How errors are raised is in [Error handling](../error-handling/README.md).

## The envelope

Every response has these four fields:

| Field | On success | On error |
|---|---|---|
| `success` | `true` | `false` |
| `data` | The result: one object, a list, or `null` when there's nothing to return | `null` |
| `error` | `null` | `code`, `message`, `details` |
| `meta` | `timestamp`, plus `pagination` on lists | `timestamp` |

Compare [get-order.200.json](examples/orders/get-order.200.json) (success) with [get-order.404.json](examples/orders/get-order.404.json) (error).

In `error`, the frontend checks **`code`** (for example `ORDER_NOT_FOUND`), which never changes once released. `message` is for people, and `details` lists invalid fields on a validation error.

The HTTP status is always the real one: a missing order is 404 with `success: false`, never 200.

## Returning it from a controller

Every controller method returns an `ApiResponse`:

```java
// One object → 200
return ApiResponse.ok(orderService.getOrder(id));

// A list → 200, with meta.pagination
return ApiResponse.ok(orderService.listOrders(status, pageable));   // a Page

// Nothing to return, e.g. after a delete → 200, data: null
return ApiResponse.ok();

// Created → 201 and a Location header
return ResponseEntity.created(location).body(ApiResponse.ok(created));
```

Errors are never returned. The service throws an exception and `GlobalExceptionHandler` builds the error response; see [Error handling](../error-handling/README.md).

## Status codes

| Action | Method and URL | Status |
|---|---|---|
| Read one | `GET /api/orders/42` | 200 |
| Read a list | `GET /api/orders?status=open` | 200 |
| Create | `POST /api/orders` | 201, with a `Location` header |
| Change with an action | `POST /api/orders/42/void` | 200, with the order after the change |
| Delete | `DELETE /api/orders/42/items/101` | 200, with `data: null` |

- **Business actions are a verb under the resource**, sent with `POST`: `/void`, `/pay`, `/send-to-kitchen`.
- **An empty list is 200 with `"data": []`**, not 404.

## Examples

Each example is a file with the real JSON Spring Boot 4.1.1 produces.

**Orders**, in [examples/orders/](examples/orders/):

| Request | Status | File |
|---|---|---|
| `GET /api/orders/42` | 200 | [get-order.200.json](examples/orders/get-order.200.json) |
| `GET /api/orders/99`, which doesn't exist | 404 | [get-order.404.json](examples/orders/get-order.404.json) |
| `GET /api/orders?status=open` | 200 | [list-orders.200.json](examples/orders/list-orders.200.json) |
| `GET /api/orders?status=completed`, no matches | 200 | [list-orders-empty.200.json](examples/orders/list-orders-empty.200.json) |
| `GET /api/orders?status=nope` | 400 | [list-orders.400.json](examples/orders/list-orders.400.json) |
| `POST /api/orders` with [create-order.request.json](examples/orders/create-order.request.json) | 201 | [create-order.201.json](examples/orders/create-order.201.json) |
| `POST /api/orders` with [create-order-invalid.request.json](examples/orders/create-order-invalid.request.json) | 400 | [create-order.400.json](examples/orders/create-order.400.json) |
| `POST /api/orders/42/void` | 200 | [void-order.200.json](examples/orders/void-order.200.json) |
| `POST /api/orders/40/void`, already paid | 409 | [void-order.409.json](examples/orders/void-order.409.json) |
| `DELETE /api/orders/42/items/101` | 200 | [remove-item.200.json](examples/orders/remove-item.200.json) |
| `DELETE /api/orders/99/items/101`, no such order | 404 | [remove-item.404.json](examples/orders/remove-item.404.json) |

**Errors any endpoint can return**, in [examples/errors/](examples/errors/):

| Cause | Status | File |
|---|---|---|
| Broken JSON | 400 | [bad-json.400.json](examples/errors/bad-json.400.json) |
| Not signed in | 401 | [not-signed-in.401.json](examples/errors/not-signed-in.401.json) |
| No permission | 403 | [forbidden.403.json](examples/errors/forbidden.403.json) |
| Unknown URL | 404 | [unknown-url.404.json](examples/errors/unknown-url.404.json) |
| Wrong HTTP method | 405 | [wrong-method.405.json](examples/errors/wrong-method.405.json) |
| Wrong content type | 415 | [unsupported-type.415.json](examples/errors/unsupported-type.415.json) |
| Unexpected server error | 500 | [unexpected.500.json](examples/errors/unexpected.500.json) |

## Lists

List endpoints take paging parameters and return the page numbers in `meta.pagination`, as in [list-orders.200.json](examples/orders/list-orders.200.json).

| Query parameter | Meaning | Default |
|---|---|---|
| `page` | Page number, starting at **0** | `0` |
| `size` | Items per page | `20` |
| `sort` | Entity field and direction, e.g. `openedAt,desc` | Newest first |

Limit the page size in `application.properties`:

```properties
spring.data.web.pageable.max-page-size=100
```

## Mapper

Each feature has a mapper in `mapper/` that copies an entity into its response DTO. [OrderMapper.java](order/mapper/OrderMapper.java) is a plain class:

| Entity `Order` | Response `OrderResponse` |
|---|---|
| `orderNo` | `orderNumber` |
| `taxAmount` | `tax` |
| `OrderItem.qty` | `quantity` |
| `openedAt` without a time zone | `openedAt` with `+07:00` |
| `tableIds` | `tableName`, e.g. `"T5"`, or `"T5, T6"` after a merge |

Columns the screen doesn't need (`storeId`, `userId` …) are left out. The service calls it at the end of each method:

```java
return orderMapper.toResponse(order, tableService.getTableNames(order.getTableIds()));
```

## JSON conventions

| Data | Format | Example |
|---|---|---|
| Field names | camelCase | `orderNumber`, `openedAt` |
| Enums | The lowercase database value | `"orderType": "dine_in"`, also in URLs: `?status=open` |
| Money | A number with two decimals; `BigDecimal` in Java | `"total": 300.00` |
| Date and time | ISO 8601 with the time zone | `"openedAt": "2026-10-16T12:30:00+07:00"` |
| Missing value | `null`, the field is still present | `"tableName": null` for takeaway |
| Empty list | `[]`, never `null` | `"items": []` |

Enums use the same lowercase value everywhere: `DbEnum` for JSON, `WebConfig` for URLs, and each enum's `JpaConverter` (built on `DbEnumConverter`) for the database.

## Files

Copy each Java file into the backend at the same package path under `backend/src/main/java/com/astronity/pos/`.

```
api-response/
├── examples/                          ← JSON examples (reference only, not copied)
├── common/
│   ├── response/
│   │   ├── ApiResponse.java           ← the envelope: ok(...) and fail(...)
│   │   ├── ApiError.java              ← code, message, details
│   │   ├── Meta.java                  ← timestamp, pagination
│   │   └── Pagination.java
│   ├── enums/
│   │   ├── DbEnum.java                ← lowercase enum values in JSON
│   │   └── DbEnumConverter.java       ← … in the database
│   └── config/
│       └── WebConfig.java             ← … in URLs
└── order/                             ← a complete example feature
    ├── controller/OrderController.java  ← returns ApiResponse, with Swagger annotations
    ├── service/OrderService.java
    ├── entity/Order.java, OrderItem.java
    ├── enums/OrderType.java, OrderStatus.java, OrderItemStatus.java
    ├── dto/CreateOrderRequest.java, OrderResponse.java, OrderItemResponse.java
    └── mapper/OrderMapper.java          ← entity → DTO
```

`GlobalExceptionHandler`, the exception classes and `SecurityConfig` are in [error-handling/](../error-handling/README.md).

## Rules

- **Every controller method returns `ApiResponse`.** Never an entity, never a bare DTO.
- **Errors are thrown, not returned.** No `ApiResponse.fail(...)` in controllers or services.
- **Use the real HTTP status.** Never 200 with `success: false`.
- **A released field or error code is never renamed or removed.** Add new fields instead.

## In Angular

```typescript
export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: { code: string; message: string; details: { field: string; message: string }[] } | null;
  meta: { timestamp: string; pagination?: { page: number; size: number; totalElements: number; totalPages: number } };
}

this.http.get<ApiResponse<OrderResponse>>(`/api/orders/${id}`)
  .subscribe(res => this.order = res.data!);

// Errors have the same shape, so one interceptor can handle all of them
error: (err: HttpErrorResponse) => this.showError(err.error.error.code, err.error.error.message)
```

## Testing

```java
mockMvc.perform(get("/api/orders/42"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.orderType").value("dine_in"));
```

## Related docs

- [Error handling](../error-handling/README.md): how exceptions become error responses
- [Project structure](../project-structure/README.md): where each package sits
