package pl.szczesnaj.customersapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(
        name="CUSTOMER",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"peselNumber"})
)
@Entity
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surname;
    private String peselNumber;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private CommunicationMethods contacts;
}
