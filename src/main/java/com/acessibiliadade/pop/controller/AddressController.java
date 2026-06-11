package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.model.Address;
import com.acessibiliadade.pop.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/user/{userId}")
    public Address createAddress(@PathVariable UUID userId, @RequestBody Address address) {
        return addressService.createAddress(userId, address);
    }

    @GetMapping("/user/{userId}")
    public List<Address> getAddressesByUser(@PathVariable UUID userId) {
        return addressService.getAddressesByUserId(userId);
    }

    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
    }
}