package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.exception.ResourceNotFoundException;
import com.acessibiliadade.pop.model.Address;
import com.acessibiliadade.pop.model.User;
import com.acessibiliadade.pop.repository.AddressRepository;
import com.acessibiliadade.pop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public Address createAddress(UUID userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        // Ignora qualquer user vindo no corpo — o dono do endereço é sempre o do path,
        // já validado como o usuário autenticado no controller.
        address.setUser(user);
        address.setId(null);
        return addressRepository.save(address);
    }

    public List<Address> getAddressesByUserId(UUID userId) {
        return addressRepository.findByUserId(userId);
    }

    // Ownership pelo id do próprio endereço: /addresses/{id} não tem userId no path,
    // então precisamos buscar e comparar com o usuário autenticado.
    public void deleteAddress(Long addressId, UUID callerUserId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado: " + addressId));
        if (address.getUser() == null || !callerUserId.equals(address.getUser().getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar este endereço");
        }
        addressRepository.delete(address);
    }
}
