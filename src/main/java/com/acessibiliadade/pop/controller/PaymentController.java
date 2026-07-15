package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.enums.PaymentStatus;
import com.acessibiliadade.pop.model.Payment;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthorizationService authorizationService;

    @PostMapping("/order/{orderId}")
    public Payment createPayment(@PathVariable Long orderId,
                                 @RequestParam String method) {
        authorizationService.assertOrderOwnership(orderId);
        return paymentService.createPayment(orderId, method);
    }

    @PostMapping("/{paymentId}/process")
    public Payment processPayment(@PathVariable Long paymentId) {
        assertPaymentOwnership(paymentId);
        return paymentService.processPayment(paymentId);
    }

    @PostMapping("/{paymentId}/approve")
    public Payment approvePayment(@PathVariable Long paymentId) {
        assertPaymentOwnership(paymentId);
        return paymentService.approvePayment(paymentId);
    }

    @PostMapping("/{paymentId}/decline")
    public Payment declinePayment(@PathVariable Long paymentId) {
        assertPaymentOwnership(paymentId);
        return paymentService.declinePayment(paymentId);
    }

    @PostMapping("/{paymentId}/refund")
    public Payment refundPayment(@PathVariable Long paymentId) {
        assertPaymentOwnership(paymentId);
        return paymentService.refundPayment(paymentId);
    }

    @GetMapping("/order/{orderId}")
    public Payment getPaymentByOrder(@PathVariable Long orderId) {
        authorizationService.assertOrderOwnership(orderId);
        return paymentService.getPaymentByOrder(orderId);
    }

    @GetMapping("/status/{status}")
    public List<Payment> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        UUID userId = authorizationService.currentUser().getId();
        return paymentService.getPaymentsByStatus(status, userId);
    }

    @PutMapping("/{paymentId}/status")
    public Payment updateStatus(@PathVariable Long paymentId,
                                @RequestParam PaymentStatus status) {
        assertPaymentOwnership(paymentId);
        return paymentService.updatePaymentStatus(paymentId, status);
    }

    private void assertPaymentOwnership(Long paymentId) {
        UUID userId = authorizationService.currentUser().getId();
        paymentService.assertOwnership(paymentId, userId);
    }
}
