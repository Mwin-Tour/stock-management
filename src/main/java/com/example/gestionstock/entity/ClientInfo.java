package com.example.gestionstock.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
public class ClientInfo {
    private String clientName;
    private String clientAddress;
    private String clientPhoneNumber;
}
