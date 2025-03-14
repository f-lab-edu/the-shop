package com.flab.theshop.controller;

import com.flab.theshop.dto.order.OrderRequest;
import com.flab.theshop.dto.order.OrderResponse;
import com.flab.theshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.flab.theshop.exception.SuccessMessage.ORDER_CANCEL_OK;
import static com.flab.theshop.exception.SuccessMessage.ORDER_OK;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Response<OrderResponse> order(@Valid @RequestBody OrderRequest request) {
        Long orderId = orderService.order(request);
        return Response.success(ORDER_OK.getMessage(), new OrderResponse(orderId));
    }

    @DeleteMapping("/{orderId}")
    public Response<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return Response.success(ORDER_CANCEL_OK.getMessage());
    }
}
