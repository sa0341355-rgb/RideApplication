package com.paymentservice.controller;

import com.paymentservice.dto.PaymentVerificationRequest;
import com.paymentservice.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/order")
    public String createOrder(
            @RequestParam Long rideId,
            @RequestParam Double amount)
            throws RazorpayException {

        return paymentService.createOrder(
                rideId,
                amount
        );
    }


     @PostMapping("/verify")
    public String verifyPayment(
            @RequestBody PaymentVerificationRequest request) {

        boolean verified =
                paymentService.verifyPayment(request);

        if (verified) {
            return "Payment verified successfully";
        }

        return "Payment verification failed";
    }
}