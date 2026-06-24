package com.acessibiliadade.pop.repository;

import com.acessibiliadade.pop.model.UserAccessibilitySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccessibilitySettingsRepository extends JpaRepository<UserAccessibilitySettings, Long> {
    // CORRIGIDO: User.id é UUID
    Optional<UserAccessibilitySettings> findByUserId(UUID userId);
}
