package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.model.Phone;
import com.acessibiliadade.pop.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/phones")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @PostMapping("/user/{userId}")
    public Phone addPhone(@PathVariable UUID userId,
                          @RequestParam String number) {
        return phoneService.addPhone(userId, number);
    }

    @GetMapping("/user/{userId}")
    public List<Phone> getUserPhones(@PathVariable UUID userId) {
        return phoneService.getUserPhones(userId);
    }

    @PutMapping("/{phoneId}")
    public Phone updatePhone(@PathVariable Long phoneId,
                             @RequestParam String number) {
        return phoneService.updatePhone(phoneId, number);
    }

    @DeleteMapping("/{phoneId}")
    public void deletePhone(@PathVariable Long phoneId) {
        phoneService.deletePhone(phoneId);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteAllPhones(@PathVariable UUID userId) {
        phoneService.deleteAllUserPhones(userId);
    }
}