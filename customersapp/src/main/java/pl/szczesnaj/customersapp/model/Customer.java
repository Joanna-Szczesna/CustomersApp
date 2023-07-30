/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Table(
        name = "CUSTOMER",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"peselNumber"})
)
@Entity
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "PESEL number cannot be null or empty")
    @Size(min = 11, max = 11, message = "PESEL should be 11 digits")
    @Pattern(regexp="[\\d]{11}", message = "PESEL should contain only digits")
    private String peselNumber;

    @NotEmpty(message = "Name cannot be null or empty")
    @Size(min = 3, max = 30, message = "Name between 3 and 30 characters")
    private String name;

    @NotEmpty(message = "Surname cannot be null or empty")
    @Size(min = 3, max = 40,  message = "Surname between 3 and 40 characters")
    private String surname;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private CommunicationMethods contacts;
}
