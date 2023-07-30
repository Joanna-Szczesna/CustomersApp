/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.szczesnaj.customersapp.model.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c left join fetch c.contacts")
    List<Customer> findAllCustomers();

    @Query("select c from Customer c where peselNumber = :peselNumber")
    Optional<Customer> findCustomerByPeselNum(@Param("peselNumber") String peselNumber);
}
