package com.chhotu.billing_software.service.impl;

import com.chhotu.billing_software.entity.OrderEntity;
import com.chhotu.billing_software.entity.OrderItemEntity;
import com.chhotu.billing_software.io.*;
import com.chhotu.billing_software.repository.OrderEntityRepository;
import com.chhotu.billing_software.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    // Inject the OrderEntityRepository for interacting with Order entities in the database
    private final OrderEntityRepository orderEntityRepository;

    /**
     * Creates a new order in the system, including order items and payment details.
     * @param request The order request containing customer information, items, and payment method.
     * @return The response containing the created order details.
     */
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        // Convert the request to an OrderEntity
        OrderEntity newOrder = convertToOrderEntity(request);

        // Create payment details based on the payment method (completed for cash, pending otherwise)
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setStatus(newOrder.getPaymentMethod() == PaymentMethod.CASH ?
                PaymentDetails.PaymentStatus.COMPLETED : PaymentDetails.PaymentStatus.PENDING);
        newOrder.setPaymentDetails(paymentDetails);

        // Convert cart items from the request to OrderItemEntity and associate them with the order
        List<OrderItemEntity> orderItems = request.getCartItems().stream()
                .map(this::convertToOrderItemEntity)
                .collect(Collectors.toList());
        newOrder.setItems(orderItems);

        // Save the order in the repository
        orderEntityRepository.save(newOrder);

        // Convert the saved order to a response object and return it
        return convertToResponse(newOrder);
    }

    /**
     * Converts an OrderItemRequest object to an OrderItemEntity object.
     * @param orderItemRequest The OrderItemRequest to be converted.
     * @return The converted OrderItemEntity object.
     */
    private OrderItemEntity convertToOrderItemEntity(OrderRequest.OrderItemRequest orderItemRequest) {
        return OrderItemEntity.builder()
                .itemId(orderItemRequest.getItemId())
                .name(orderItemRequest.getName())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .build();
    }

    /**
     * Converts an OrderEntity object to an OrderResponse object.
     * @param newOrder The OrderEntity to be converted.
     * @return The converted OrderResponse object.
     */
    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .orderId(newOrder.getOrderId())
                .customerName(newOrder.getCustomerName())
                .phoneNumber(newOrder.getPhoneNumber())
                .subtotal(newOrder.getSubtotal())
                .tax(newOrder.getTax())
                .grandTotal(newOrder.getGrandTotal())
                .paymentMethod(newOrder.getPaymentMethod())
                .items(newOrder.getItems().stream()
                        .map(this::convertToItemResponse)
                        .collect(Collectors.toList()))
                .paymentDetails(newOrder.getPaymentDetails())
                .createdAt(newOrder.getCreatedAt())
                .build();
    }

    /**
     * Converts an OrderItemEntity object to an OrderItemResponse object.
     * @param orderItemEntity The OrderItemEntity to be converted.
     * @return The converted OrderItemResponse object.
     */
    private OrderResponse.OrderItemResponse convertToItemResponse(OrderItemEntity orderItemEntity) {
        return OrderResponse.OrderItemResponse.builder()
                .itemId(orderItemEntity.getItemId())
                .name(orderItemEntity.getName())
                .price(orderItemEntity.getPrice())
                .quantity(orderItemEntity.getQuantity())
                .build();
    }

    /**
     * Converts an OrderRequest object to an OrderEntity object.
     * @param request The OrderRequest to be converted.
     * @return The converted OrderEntity object.
     */
    private OrderEntity convertToOrderEntity(OrderRequest request) {
        return OrderEntity.builder()
                .customerName(request.getCustomerName())
                .phoneNumber(request.getPhoneNumber())
                .subtotal(request.getSubtotal())
                .tax(request.getTax())
                .grandTotal(request.getGrandTotal())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .build();
    }

    /**
     * Deletes an order from the system by its ID.
     * @param orderId The ID of the order to be deleted.
     */
    @Override
    public void deleteOrder(String orderId) {
        // Find the order by its ID
        OrderEntity existingOrder = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Delete the order from the repository
        orderEntityRepository.delete(existingOrder);
    }

    /**
     * Retrieves the latest orders from the system.
     * @return A list of OrderResponse objects representing the latest orders.
     */
    @Override
    public List<OrderResponse> getLatestOrders() {
        // Fetch all orders ordered by their creation date in descending order, and convert to response
        return orderEntityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Verifies payment for a specific order using Razorpay's signature.
     * @param request The payment verification request containing Razorpay order and payment details.
     * @return The OrderResponse containing the updated order details after payment verification.
     */
    @Override
    public OrderResponse verifyPayment(PaymentVerificationRequest request) {
        // Find the order by its ID
        OrderEntity existingOrder = orderEntityRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify the payment signature using Razorpay details
        if (!verifyRazorpaySignature(request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature())) {
            throw new RuntimeException("Payment verification failed: Invalid signature");
        }

        // Update the payment details and set status to completed
        PaymentDetails paymentDetails = existingOrder.getPaymentDetails();
        paymentDetails.setRazorpayOrderId(request.getRazorpayOrderId());
        paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
        paymentDetails.setRazorpaySignature(request.getRazorpaySignature());
        paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);

        // Save the updated order
        existingOrder = orderEntityRepository.save(existingOrder);

        // Return the updated order details in response
        return convertToResponse(existingOrder);
    }

    /**
     * Calculates the total sales for a specific date.
     * @param date The date for which sales should be calculated.
     * @return The total sales amount for the given date.
     */
    @Override
    public Double sumSalesByDate(LocalDate date) {
        return orderEntityRepository.sumSalesByDate(date);
    }

    /**
     * Counts the number of orders placed on a specific date.
     * @param date The date for which the order count should be calculated.
     * @return The number of orders placed on the given date.
     */
    @Override
    public Long countByOrderDate(LocalDate date) {
        return orderEntityRepository.countByOrderDate(date);
    }

    /**
     * Retrieves a list of the most recent orders (up to 5 orders).
     * @return A list of the most recent OrderResponse objects.
     */
    @Override
    public List<OrderResponse> findRecentOrders() {
        return orderEntityRepository.findRecentOrders(PageRequest.of(0, 5))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Verifies the Razorpay payment signature (to be implemented for production).
     * @param razorpayOrderId The Razorpay order ID.
     * @param razorpayPaymentId The Razorpay payment ID.
     * @param razorpaySignature The Razorpay payment signature.
     * @return true if the signature is valid, false otherwise.
     */
    private boolean verifyRazorpaySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        return true;

    //this code is for production baad ,e apan isko krenge
//        try {
//            String secret = "your_secret_key"; // 🔐 Replace with your Razorpay secret key
//            String payload = razorpayOrderId + "|" + razorpayPaymentId;
//
//            Mac mac = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
//            mac.init(secretKeySpec);
//
//            byte[] digest = mac.doFinal(payload.getBytes());
//            String generatedSignature = new String(Base64.getEncoder().encode(digest));
//
//            return generatedSignature.equals(razorpaySignature);
//        } catch (Exception e) {
//            // Log the error for debugging
//            System.err.println("Error verifying Razorpay signature: " + e.getMessage());
//            return false;
//        }

    }
}
