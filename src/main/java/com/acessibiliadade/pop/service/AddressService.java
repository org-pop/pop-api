package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.model.Address;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.repository.AddressRepository;
import com.acessibiliadade.pop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    // Criar endereço para um usuário
    public Address createAddress(UUID userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUser(user);
        return addressRepository.save(address);
    }

    // Listar todos endereços de um usuário
    public List<Address> getAddressesByUserId(UUID userId) {
        return addressRepository.findByUserId(userId);
    }

    // Deletar endereço
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}