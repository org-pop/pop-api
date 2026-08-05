package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.enums.PaymentStatus;
import com.acessibiliadade.pop.model.Payment;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthorizationService authorizationService;

    // Criação: o dono do pedido inicia o pagamento (fica em PENDING).
    @PostMapping("/order/{orderId}")
    public Payment createPayment(@PathVariable Long orderId,
                                 @RequestParam String method) {
        authorizationService.assertOrderOwnership(orderId);
        return paymentService.createPayment(orderId, method);
    }

    // Transições de status (process/approve/decline/refund + PUT status) representam
    // a autoridade do gateway/administrador, não do comprador. Sem esse gate, o dono
    // do pedido aprovava o próprio pagamento sem transferir nada.
    @PostMapping("/{paymentId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public Payment processPayment(@PathVariable Long paymentId) {
        return paymentService.processPayment(paymentId);
    }

    @PostMapping("/{paymentId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Payment approvePayment(@PathVariable Long paymentId) {
        return paymentService.approvePayment(paymentId);
    }

    @PostMapping("/{paymentId}/decline")
    @PreAuthorize("hasRole('ADMIN')")
    public Payment declinePayment(@PathVariable Long paymentId) {
        return paymentService.declinePayment(paymentId);
    }

    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public Payment refundPayment(@PathVariable Long paymentId) {
        return paymentService.refundPayment(paymentId);
    }

    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Payment updateStatus(@PathVariable Long paymentId,
                                @RequestParam PaymentStatus status) {
        return paymentService.updatePaymentStatus(paymentId, status);
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
}
