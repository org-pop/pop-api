package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.model.Phone;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.repository.PhoneRepository;
import com.acessibiliadade.pop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PhoneService {

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private UserRepository userRepository;

    public Phone addPhone(UUID userId, String number) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Phone phone = new Phone();
        phone.setUser(user);
        phone.setNumber(number);

        return phoneRepository.save(phone);
    }

    public List<Phone> getUserPhones(UUID userId) {
        return phoneRepository.findByUserId(userId);
    }

    public void deletePhone(Long phoneId) {
        phoneRepository.deleteById(phoneId);
    }

    public void deleteAllUserPhones(UUID userId) {
        phoneRepository.deleteByUserId(userId);
    }

    public Phone updatePhone(Long phoneId, String newNumber) {
        Phone phone = phoneRepository.findById(phoneId)
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        phone.setNumber(newNumber);
        return phoneRepository.save(phone);
    }
}