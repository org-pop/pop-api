package com.acessibiliadade.pop.service;

import java.util.List;
import java.util.UUID;
import com.acessibiliadade.pop.enums.PaymentStatus;
import com.acessibiliadade.pop.exception.BusinessException;
import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.Order;
import com.acessibiliadade.pop.model.Payment;
import com.acessibiliadade.pop.repository.OrderRepository;
import com.acessibiliadade.pop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment createPayment(Long orderId, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));

        // A unique constraint no banco já garante um pagamento por pedido, mas checamos
        // aqui para devolver 400 com mensagem clara em vez de 500 por violação de constraint.
        paymentRepository.findByOrderId(orderId).ifPresent(existing -> {
            throw new BusinessException("Já existe um pagamento para este pedido (id " + existing.getId() + ")");
        });

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));

        // Simular processamento (aqui viria integração real com gateway)
        // Por simulação, vamos aprovar
        payment.setStatus(PaymentStatus.APPROVED);

        return paymentRepository.save(payment);
    }

    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para o pedido: " + orderId));
    }

    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));

        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment approvePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));

        payment.setStatus(PaymentStatus.APPROVED);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment declinePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));

        payment.setStatus(PaymentStatus.DECLINED);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));

        if (payment.getStatus() != PaymentStatus.APPROVED) {
            throw new BusinessException("Apenas pagamentos aprovados podem ser estornados");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status, UUID userId) {
        return paymentRepository.findByStatusAndOrder_User_Id(status, userId);
    }

    // Precisa ser @Transactional para atravessar Payment -> Order -> User (ambos LAZY)
    // sem estourar LazyInitializationException fora da sessão do Hibernate.
    @Transactional(readOnly = true)
    public void assertOwnership(Long paymentId, UUID userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));
        Order order = payment.getOrder();
        if (order == null || order.getUser() == null || !userId.equals(order.getUser().getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar este pagamento");
        }
    }
}