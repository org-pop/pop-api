package com.acessibiliadade.pop.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

    // Máquina de estados. Sem isso updatePaymentStatus aceitava qualquer coisa —
    // REFUNDED->APPROVED, DECLINED->APPROVED — e o próprio dono do pedido driblava
    // o fluxo. Terminais (DECLINED, REFUNDED, CANCELLED) não têm saída.
    private static final Map<PaymentStatus, Set<PaymentStatus>> ALLOWED_TRANSITIONS = Map.of(
            PaymentStatus.PENDING,    Set.of(PaymentStatus.PROCESSING, PaymentStatus.CANCELLED),
            PaymentStatus.PROCESSING, Set.of(PaymentStatus.APPROVED, PaymentStatus.DECLINED),
            PaymentStatus.APPROVED,   Set.of(PaymentStatus.REFUNDED),
            PaymentStatus.DECLINED,   Set.of(),
            PaymentStatus.REFUNDED,   Set.of(),
            PaymentStatus.CANCELLED,  Set.of()
    );

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
        return transitionTo(paymentId, PaymentStatus.PROCESSING);
    }

    @Transactional
    public Payment approvePayment(Long paymentId) {
        return transitionTo(paymentId, PaymentStatus.APPROVED);
    }

    @Transactional
    public Payment declinePayment(Long paymentId) {
        return transitionTo(paymentId, PaymentStatus.DECLINED);
    }

    @Transactional
    public Payment refundPayment(Long paymentId) {
        return transitionTo(paymentId, PaymentStatus.REFUNDED);
    }

    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        return transitionTo(paymentId, status);
    }

    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para o pedido: " + orderId));
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

    private Payment transitionTo(Long paymentId, PaymentStatus target) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado: " + paymentId));

        PaymentStatus current = payment.getStatus();
        if (current == target) {
            throw new BusinessException("Pagamento já está no status " + target);
        }
        Set<PaymentStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(target)) {
            throw new BusinessException("Transição de pagamento inválida: " + current + " -> " + target);
        }

        payment.setStatus(target);
        return paymentRepository.save(payment);
    }
}
