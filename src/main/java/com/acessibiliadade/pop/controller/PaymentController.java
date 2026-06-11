package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.enums.PaymentStatus;
import com.acessibiliadade.pop.model.Payment;
import com.acessibiliadade.pop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/order/{orderId}")
    public Payment createPayment(@PathVariable Long orderId,
                                 @RequestParam String method) {
        return paymentService.createPayment(orderId, method);
    }

    @PostMapping("/{paymentId}/process")
    public Payment processPayment(@PathVariable Long paymentId) {
        return paymentService.processPayment(paymentId);
    }

    @PostMapping("/{paymentId}/approve")
    public Payment approvePayment(@PathVariable Long paymentId) {
        return paymentService.approvePayment(paymentId);
    }

    @PostMapping("/{paymentId}/decline")
    public Payment declinePayment(@PathVariable Long paymentId) {
        return paymentService.declinePayment(paymentId);
    }

    @PostMapping("/{paymentId}/refund")
    public Payment refundPayment(@PathVariable Long paymentId) {
        return paymentService.refundPayment(paymentId);
    }

    @GetMapping("/order/{orderId}")
    public Payment getPaymentByOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentByOrder(orderId);
    }

    @GetMapping("/status/{status}")
    public List<Payment> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return paymentService.getPaymentsByStatus(status);
    }

    @PutMapping("/{paymentId}/status")
    public Payment updateStatus(@PathVariable Long paymentId,
                                @RequestParam PaymentStatus status) {
        return paymentService.updatePaymentStatus(paymentId, status);
    }
}