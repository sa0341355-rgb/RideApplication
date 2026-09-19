package com.paymentservice.service;


import com.paymentservice.dto.PaymentVerificationRequest;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public PaymentService(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    public String createOrder(
            Long rideId,
            Double amount) throws RazorpayException {

        JSONObject orderRequest = new JSONObject();

        orderRequest.put(
                "amount",
                Math.round(amount * 100)
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                "ride_" + rideId
        );

        Order order =
                razorpayClient.orders.create(orderRequest);

        return order.toString();
    }

    public boolean verifyPayment(
            PaymentVerificationRequest request) {

        try {

            String payload =
                    request.getRazorpayOrderId()
                            + "|"
                            + request.getRazorpayPaymentId();

            String generatedSignature =
                    hmacSha256(
                            payload,
                            razorpayKeySecret
                    );

            return generatedSignature.equals(
                    request.getRazorpaySignature()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Payment verification failed",
                    e
            );
        }
    }

    private String hmacSha256(
            String data,
            String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );

        mac.init(secretKey);

        byte[] hash =
                mac.doFinal(
                        data.getBytes(StandardCharsets.UTF_8)
                );

        StringBuilder hexString =
                new StringBuilder();

        for (byte b : hash) {
            String hex =
                    Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }
}