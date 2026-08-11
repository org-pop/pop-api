package com.acessibiliadade.pop.dto;

import com.acessibiliadade.pop.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressDTOs {

    public record AddressRequest(
            @NotBlank(message = "Rua é obrigatória")
            String street,

            @NotBlank(message = "Número é obrigatório")
            String number,

            @NotBlank(message = "Cidade é obrigatória")
            String city,

            @NotBlank(message = "Estado é obrigatório")
            @Size(min = 2, max = 2, message = "Estado deve ser a UF com 2 letras")
            String state,

            @NotBlank(message = "CEP é obrigatório")
            @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
            String zipCode
    ) {}

    public record AddressResponse(
            Long id,
            String street,
            String number,
            String city,
            String state,
            String zipCode
    ) {
        public static AddressResponse from(Address address) {
            return new AddressResponse(
                    address.getId(),
                    address.getStreet(),
                    address.getNumber(),
                    address.getCity(),
                    address.getState(),
                    address.getZipCode()
            );
        }
    }
}
