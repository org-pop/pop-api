package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.model.Phone;
import com.acessibiliadade.pop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    List<Phone> findByUserId(UUID userId);

    List<Phone> findByUser(User user);

    void deleteByUserId(UUID userId);
}