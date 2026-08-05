package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.Phone;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.repository.PhoneRepository;
import com.acessibiliadade.pop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhoneService {

    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;

    public Phone addPhone(UUID userId, String number) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        Phone phone = new Phone();
        phone.setUser(user);
        phone.setNumber(number);
        return phoneRepository.save(phone);
    }

    public List<Phone> getUserPhones(UUID userId) {
        return phoneRepository.findByUserId(userId);
    }

    // Endpoints com /{phoneId} não carregam o userId no path; o service busca o dono
    // e compara com o autenticado antes de mexer no recurso.
    public void deletePhone(Long phoneId, UUID callerUserId) {
        Phone phone = loadOwned(phoneId, callerUserId);
        phoneRepository.delete(phone);
    }

    public Phone updatePhone(Long phoneId, String newNumber, UUID callerUserId) {
        Phone phone = loadOwned(phoneId, callerUserId);
        phone.setNumber(newNumber);
        return phoneRepository.save(phone);
    }

    // deleteByUserId é derived query — sem @Transactional o Spring Data recusa a
    // deleção em massa. Adicionar aqui para não depender de open-in-view.
    @Transactional
    public void deleteAllUserPhones(UUID userId) {
        phoneRepository.deleteByUserId(userId);
    }

    private Phone loadOwned(Long phoneId, UUID callerUserId) {
        Phone phone = phoneRepository.findById(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Telefone não encontrado: " + phoneId));
        if (phone.getUser() == null || !callerUserId.equals(phone.getUser().getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar este telefone");
        }
        return phone;
    }
}
