package com.chhotu.billing_software.controller;

import com.chhotu.billing_software.io.OrderResponse;
import com.chhotu.billing_software.io.PaymentRequest;
import com.chhotu.billing_software.io.PaymentVerificationRequest;
import com.chhotu.billing_software.io.RazorpayOrderResponse;
import com.chhotu.billing_software.service.OrderService;
import com.chhotu.billing_software.service.RazorpayService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService; // Service for handling Razorpay-related logic
    private final OrderService orderService; // Service to handle order verification and updates

    /**
     * Endpoint to create a Razorpay order.
     * Method: POST
     * URL: /payments/create-order
     * Request Body: PaymentRequest (contains amount and currency)
     * Response: RazorpayOrderResponse (contains orderId and payment info)
     */
    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 CREATED on success
    public RazorpayOrderResponse createRazorpayOrder(@RequestBody PaymentRequest request) throws RazorpayException {
        return razorpayService.createOrder(request.getAmount(), request.getCurrency());
    }

    /**
     * Endpoint to verify the payment after it's made.
     * Method: POST
     * URL: /payments/verify
     * Request Body: PaymentVerificationRequest (contains payment details for verification)
     * Response: OrderResponse (updated order with payment status)
     */
    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return orderService.verifyPayment(request); // Verifies payment and updates order status
    }
}
