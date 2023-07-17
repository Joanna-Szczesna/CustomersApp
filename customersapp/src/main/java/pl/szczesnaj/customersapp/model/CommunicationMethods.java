package pl.szczesnaj.customersapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CommunicationMethods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String emailAddress;
    private String residenceAddress;
    private String registeredAddress;
    private String privatePhoneNumber;
    private String businessPhoneNumber;

}
