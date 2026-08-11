package com.acessibiliadade.pop.dto;

import com.acessibiliadade.pop.enums.PaymentStatus;
import com.acessibiliadade.pop.model.Payment;

public class PaymentDTOs {

    public record PaymentResponse(Long id, Long orderId, String method, PaymentStatus status) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                    payment.getId(),
                    payment.getOrder() != null ? payment.getOrder().getId() : null,
                    payment.getMethod(),
                    payment.getStatus()
            );
        }
    }
}
