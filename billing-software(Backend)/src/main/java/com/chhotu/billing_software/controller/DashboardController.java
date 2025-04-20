package com.chhotu.billing_software.controller;


import com.chhotu.billing_software.io.DashboardResponse;
import com.chhotu.billing_software.io.OrderResponse;
import com.chhotu.billing_software.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrderService orderService; // Injected service to handle order-related operations

    /**
     * Fetches today's dashboard summary including:
     * - Total sales for today
     * - Number of orders placed today
     * - List of recent orders
     */
    @GetMapping
    public DashboardResponse getDashboardData() {
        LocalDate today = LocalDate.now(); // Get the current date

        // Get total sales made today
        Double todaySale = orderService.sumSalesByDate(today);

        // Get total number of orders placed today
        Long todayOrderCount = orderService.countByOrderDate(today);

        // Get the list of recent orders (could be last 5 or 10, as implemented in the service)
        List<OrderResponse> recentOrders = orderService.findRecentOrders();

        // Return a response object with fallback values (0.0 or 0) if data is null
        return new DashboardResponse(
                todaySale != null ? todaySale : 0.0,
                todayOrderCount != null ? todayOrderCount : 0,
                recentOrders
        );
    }
}
