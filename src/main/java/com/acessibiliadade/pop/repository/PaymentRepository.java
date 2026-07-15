package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.enums.PaymentStatus;
import com.acessibiliadade.pop.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByStatusAndOrder_User_Id(PaymentStatus status, UUID userId);
}