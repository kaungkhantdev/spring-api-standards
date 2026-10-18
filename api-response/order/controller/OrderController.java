package com.astronity.pos.order.controller;

import com.astronity.pos.common.response.ApiResponse;
import com.astronity.pos.order.dto.CreateOrderRequest;
import com.astronity.pos.order.dto.OrderResponse;
import com.astronity.pos.order.enums.OrderStatus;
import com.astronity.pos.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Every method returns ApiResponse.ok(...). Errors are thrown as exceptions and
 * GlobalExceptionHandler turns them into ApiResponse.fail(...).
 * Example JSON for every case: api-response/examples/orders/.
 * The @Tag, @Operation and @ApiResponse annotations describe each endpoint in Swagger UI (/swagger-ui.html).
 */
@Tag(name = "Orders", description = "Create, view and change orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get one order")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The order")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "ORDER_NOT_FOUND: the order doesn't exist")
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@Parameter(description = "Order id", example = "42") @PathVariable Long id) {
        return ApiResponse.ok(orderService.getOrder(id));
    }

    @Operation(summary = "List orders", description = "Newest first, 20 per page. Leave status empty for all orders.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "A page of orders; an empty page if nothing matches")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "BAD_REQUEST: unknown status")
    @GetMapping
    public ApiResponse<List<OrderResponse>> listOrders(
            @Parameter(description = "Only orders with this status", example = "open")
            @RequestParam(required = false) OrderStatus status,
            @ParameterObject
            @PageableDefault(size = 20, sort = "openedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(orderService.listOrders(status, pageable));
    }

    @Operation(summary = "Create an order")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "The new order; the Location header has its URL")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "VALIDATION_FAILED: a field is missing or invalid")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.createOrder(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok(created));
    }

    @Operation(summary = "Void an order")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "The order after voiding")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "ORDER_NOT_FOUND: the order doesn't exist")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "ORDER_STATE_INVALID: the order is already paid")
    @PostMapping("/{id}/void")
    public ApiResponse<OrderResponse> voidOrder(@Parameter(description = "Order id", example = "42") @PathVariable Long id) {
        return ApiResponse.ok(orderService.voidOrder(id));
    }

    @Operation(summary = "Remove an item from an order")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Removed; data is null")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "ORDER_NOT_FOUND: the order or the item doesn't exist")
    @DeleteMapping("/{id}/items/{itemId}")
    public ApiResponse<Void> removeItem(
            @Parameter(description = "Order id", example = "42") @PathVariable Long id,
            @Parameter(description = "Item id", example = "101") @PathVariable Long itemId) {
        orderService.removeItem(id, itemId);
        return ApiResponse.ok();
    }
}
