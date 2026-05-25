package com.example.gestionstock.Mail;

import com.example.gestionstock.dtos.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MailStructure {
    private String subject;
    private String message;
}
