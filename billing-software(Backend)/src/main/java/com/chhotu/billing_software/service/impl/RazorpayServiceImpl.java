package com.chhotu.billing_software.service.impl;

import com.chhotu.billing_software.io.RazorpayOrderResponse;
import com.chhotu.billing_software.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    // Inject Razorpay key and secret from application properties
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    /**
     * Creates a Razorpay order for a given amount and currency.
     * @param amount The amount for the order in the currency's base unit (e.g., paise for INR).
     * @param currency The currency for the order (e.g., "INR").
     * @return A RazorpayOrderResponse containing order details from Razorpay.
     * @throws RazorpayException If the Razorpay API request fails.
     */
    @Override
    public RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException {
        // Initialize Razorpay client with the API key and secret
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        // Prepare the order request JSON object
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Convert amount to paise (1 INR = 100 paise)
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis()); // Generate a unique receipt ID
        orderRequest.put("payment_capture", 1); // Enable automatic payment capture

        // Create the order using Razorpay client
        Order order = razorpayClient.orders.create(orderRequest);

        // Convert the created order into a RazorpayOrderResponse and return it
        return convertToResponse(order);
    }

    /**
     * Converts a Razorpay Order object to a RazorpayOrderResponse object.
     * @param order The Razorpay Order object.
     * @return The converted RazorpayOrderResponse containing the order details.
     */
    private RazorpayOrderResponse convertToResponse(Order order) {
        return RazorpayOrderResponse.builder()
                .id(order.get("id")) // Extract order ID
                .entity(order.get("entity")) // Extract entity (usually "order")
                .amount(order.get("amount")) // Extract amount in paise
                .currency(order.get("currency")) // Extract currency
                .status(order.get("status")) // Extract status of the order
                .created_at(order.get("created_at")) // Extract creation timestamp
                .receipt(order.get("receipt")) // Extract receipt ID
                .build(); // Return the response object
    }
}
