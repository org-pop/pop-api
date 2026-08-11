package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.PhoneDTOs.PhoneResponse;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.PhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/phones")
@RequiredArgsConstructor
public class PhoneController {

    private final PhoneService phoneService;
    private final AuthorizationService authorizationService;

    @PostMapping("/user/{userId}")
    public PhoneResponse addPhone(@PathVariable UUID userId,
                          @RequestParam String number) {
        authorizationService.assertOwnership(userId);
        return PhoneResponse.from(phoneService.addPhone(userId, number));
    }

    @GetMapping("/user/{userId}")
    public List<PhoneResponse> getUserPhones(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return phoneService.getUserPhones(userId).stream().map(PhoneResponse::from).toList();
    }

    @PutMapping("/{phoneId}")
    public PhoneResponse updatePhone(@PathVariable Long phoneId,
                             @RequestParam String number) {
        UUID caller = authorizationService.currentUser().getId();
        return PhoneResponse.from(phoneService.updatePhone(phoneId, number, caller));
    }

    @DeleteMapping("/{phoneId}")
    public void deletePhone(@PathVariable Long phoneId) {
        UUID caller = authorizationService.currentUser().getId();
        phoneService.deletePhone(phoneId, caller);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteAllPhones(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        phoneService.deleteAllUserPhones(userId);
    }
}
