package com.chhotu.billing_software.service;

import com.chhotu.billing_software.io.RazorpayOrderResponse;
import com.razorpay.RazorpayException;

public interface RazorpayService {

    RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException;
}
