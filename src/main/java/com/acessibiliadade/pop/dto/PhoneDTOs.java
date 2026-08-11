package com.acessibiliadade.pop.dto;

import com.acessibiliadade.pop.model.Phone;

public class PhoneDTOs {

    public record PhoneResponse(Long id, String number) {
        public static PhoneResponse from(Phone phone) {
            return new PhoneResponse(phone.getId(), phone.getNumber());
        }
    }
}
