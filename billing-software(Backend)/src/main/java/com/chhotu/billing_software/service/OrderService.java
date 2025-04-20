package com.chhotu.billing_software.service;

import com.chhotu.billing_software.io.OrderRequest;
import com.chhotu.billing_software.io.OrderResponse;
import com.chhotu.billing_software.io.PaymentVerificationRequest;




import java.time.LocalDate;
import java.util.List;


public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrders();

    OrderResponse verifyPayment(PaymentVerificationRequest request);

    Double sumSalesByDate(LocalDate date);

    Long countByOrderDate(LocalDate date);

    List<OrderResponse> findRecentOrders();


}
