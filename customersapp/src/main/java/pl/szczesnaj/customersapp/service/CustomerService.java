/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.szczesnaj.customersapp.model.CommunicationMethods;
import pl.szczesnaj.customersapp.model.Customer;
import pl.szczesnaj.customersapp.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    public static final int PAGE_SIZE = 5;
    private final CustomerRepository customerRepository;

    public Page<Customer> getCustomers(int page, Sort.Direction sort) {
        return customerRepository.findAllCustomers(
                PageRequest.of(page, PAGE_SIZE,
                        Sort.by(sort, "id")));
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAllCustomers();
    }
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    //todo handle exception
    public Customer getCustomerByPeselNum(String peselNum) {
        return customerRepository.findCustomerByPeselNum(peselNum).orElseThrow();
    }

    public Optional<Customer> addContact(String peselNumber, CommunicationMethods contact) {
        Optional<Customer> customer = customerRepository.findCustomerByPeselNum(peselNumber);
        return customer.map(c -> {
            c.setContacts(contact);
            return customerRepository.save(c);
        });
    }
}

