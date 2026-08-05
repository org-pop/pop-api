package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.model.Address;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AuthorizationService authorizationService;

    @PostMapping("/user/{userId}")
    public Address createAddress(@PathVariable UUID userId, @RequestBody Address address) {
        authorizationService.assertOwnership(userId);
        return addressService.createAddress(userId, address);
    }

    @GetMapping("/user/{userId}")
    public List<Address> getAddressesByUser(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return addressService.getAddressesByUserId(userId);
    }

    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable Long addressId) {
        UUID caller = authorizationService.currentUser().getId();
        addressService.deleteAddress(addressId, caller);
    }
}
