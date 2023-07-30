/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szczesnaj.customersapp.model.CommunicationMethods;
import pl.szczesnaj.customersapp.model.Customer;
import pl.szczesnaj.customersapp.repository.CustomerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getCustomers() {
        return customerRepository.findAllCustomers();
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // todo handle exception
    public Customer getCustomerByPeselNum(String peselNum) {
        return customerRepository.findCustomerByPeselNum(peselNum).orElseThrow();
    }

    public Customer addContact(String peselNumber, CommunicationMethods contact) {
        Customer customer = customerRepository.findCustomerByPeselNum(peselNumber).orElseThrow();
        customer.setContacts(contact);
        return customerRepository.save(customer);
    }
}

