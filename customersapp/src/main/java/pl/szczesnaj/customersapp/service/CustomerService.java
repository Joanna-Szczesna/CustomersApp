/*
 * Copyright (c) 2024 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        int pageNumber = Math.max(page, 0);
        Sort.Direction sortDirection = sort != null ? sort : Sort.Direction.ASC;
        return customerRepository.findAllCustomers(
                PageRequest.of(pageNumber, PAGE_SIZE,
                        Sort.by(sortDirection, "id")));
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAllCustomers();
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    //todo handle exception Optional<Customer> ; in controller decision what doing with optional np empty optional map to status 404
    public Customer getCustomerByPeselNum(String peselNum) {
        return findByPeselNum(peselNum);
    }

    public Optional<Customer> addContact(String peselNumber, CommunicationMethods contact) {
        Optional<Customer> customer = customerRepository.findCustomerByPeselNum(peselNumber);

        return customer.map(c -> {
            c.setContacts(contact);
            return customerRepository.save(c);
        });
    }

    @Transactional
    public void deleteCustomer(String peselNum) {
        Customer customerToDelete = findByPeselNum(peselNum);
        customerRepository.delete(customerToDelete);
    }


    private Customer findByPeselNum(String peselNum) {
        return customerRepository.findCustomerByPeselNum(peselNum).orElseThrow();
    }

    @Transactional
    public Customer editCustomer(Customer customerRequest) {
        Customer customerEdited = customerRepository.findCustomerByPeselNum(customerRequest.getPeselNumber()).orElseThrow();
        customerEdited.setName(customerRequest.getName());
        customerEdited.setSurname(customerRequest.getSurname());
        customerEdited.setPeselNumber(customerRequest.getPeselNumber());
        return customerEdited;
    }
}

