package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.AddressDTOs.AddressRequest;
import com.acessibiliadade.pop.dto.AddressDTOs.AddressResponse;
import com.acessibiliadade.pop.model.Address;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.AddressService;
import jakarta.validation.Valid;
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
    public AddressResponse createAddress(@PathVariable UUID userId, @Valid @RequestBody AddressRequest request) {
        authorizationService.assertOwnership(userId);
        return AddressResponse.from(addressService.createAddress(userId, toEntity(request)));
    }

    @GetMapping("/user/{userId}")
    public List<AddressResponse> getAddressesByUser(@PathVariable UUID userId) {
        authorizationService.assertOwnership(userId);
        return addressService.getAddressesByUserId(userId).stream().map(AddressResponse::from).toList();
    }

    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable Long addressId) {
        UUID caller = authorizationService.currentUser().getId();
        addressService.deleteAddress(addressId, caller);
    }

    private Address toEntity(AddressRequest request) {
        Address address = new Address();
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        return address;
    }
}
