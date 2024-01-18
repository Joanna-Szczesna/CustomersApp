/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CommunicationMethods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Email
    private String emailAddress;

    private String residenceAddress;
    private String registeredAddress;

    @Size(min = 9, max = 11, message = "Private phone number")
    @Pattern(regexp="[\\d]{9,11}", message = "Private phone number should contain only digits")
    private String privatePhoneNumber;

    @Size(min = 0, max = 11, message = "Business phone number")
    @Pattern(regexp="[\\d]{9,11}", message = "Business phone number should contain only digits")
    private String businessPhoneNumber;
}
