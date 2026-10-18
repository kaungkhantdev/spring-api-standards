# Enum Handling

How enums are written so that the database, the JSON and the URLs all use the same lowercase value, for example `dine_in`.

## Why it's needed

The database stores lowercase values (`dine_in`, `void`), but Java constants are upper case (`DINE_IN`, `VOID`). Lowercase constants aren't possible either: `void` is a reserved word in Java. So each enum keeps its value separately, and three small classes use it everywhere:

| Where | Class | Without it |
|---|---|---|
| JSON in requests and responses | `DbEnum` (its `@JsonValue`) | JSON would show `"DINE_IN"` |
| URL parameters, e.g. `?status=open` | `WebConfig` | Only `?status=OPEN` would work |
| The database column | `DbEnumConverter`, through each enum's `JpaConverter` | Hibernate would store `DINE_IN` |

## Writing an enum

Every enum implements `DbEnum` and has a `JpaConverter`:

```java
public enum OrderStatus implements DbEnum {
    OPEN("open"),
    PAID("paid"),
    VOID("void");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Converter(autoApply = true)
    public static class JpaConverter extends DbEnumConverter<OrderStatus> {
        public JpaConverter() {
            super(OrderStatus.class);
        }
    }
}
```

The entity field needs nothing extra; `autoApply` finds the converter:

```java
@Column(nullable = false)
private OrderStatus status = OrderStatus.OPEN;
```

Full examples: [OrderType](../api-response/order/enums/OrderType.java), [OrderStatus](../api-response/order/enums/OrderStatus.java), [OrderItemStatus](../api-response/order/enums/OrderItemStatus.java).

## Files

Copy into the project at the same package path under `src/main/java/`.

```
enum-handling/
└── common/
    ├── enums/
    │   ├── DbEnum.java            ← the interface every enum implements
    │   └── DbEnumConverter.java   ← base class for each enum's JpaConverter
    └── config/
        └── WebConfig.java         ← lowercase values in URL parameters
```

## Rules

- **Never use `@Enumerated`**, neither `ORDINAL` nor `STRING`. Use the enum's `JpaConverter`.
- **The value never changes once released**, because it is stored in the database and used by the client.
- **Adding a value needs a migration** when the column is a MySQL `ENUM`.

## Related docs

- [API response](../api-response/README.md): JSON conventions, including enums
- [Project structure](../project-structure/README.md): where each feature's `enums/` package sits
