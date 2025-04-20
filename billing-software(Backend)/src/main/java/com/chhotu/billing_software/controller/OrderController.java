package com.chhotu.billing_software.controller;


import com.chhotu.billing_software.io.OrderRequest;
import com.chhotu.billing_software.io.OrderResponse;
import com.chhotu.billing_software.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService; // Injected service to handle order-related logic

    /**
     * Endpoint to create a new order.
     * Method: POST
     * URL: /orders
     * Body: JSON representing order details
     * Response: The created order details
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 status code on successful creation
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request); // Delegates to service layer to create the order
    }

    /**
     * Endpoint to delete an order by ID.
     * Method: DELETE
     * URL: /orders/{orderId}
     * Response: 204 No Content if successful
     */
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 status code on successful deletion
    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId); // Delegates deletion to the service layer
    }

    /**
     * Endpoint to get a list of the latest orders.
     * Method: GET
     * URL: /orders/latest
     * Response: List of recent orders
     */
    @GetMapping("/latest")
    public List<OrderResponse> getLatestOrders() {
        return orderService.getLatestOrders(); // Fetches recent orders from the service
    }
}
