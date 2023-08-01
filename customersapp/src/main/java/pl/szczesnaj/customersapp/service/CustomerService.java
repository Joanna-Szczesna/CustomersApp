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

@Service
@RequiredArgsConstructor
public class CustomerService {

    public static final int PAGE_SIZE = 10;
    private final CustomerRepository customerRepository;

    public Page<Customer> getCustomers(int page, Sort.Direction sort) {
        return customerRepository.findAllCustomers(
                PageRequest.of(page, PAGE_SIZE,
                        Sort.by(sort, "id")));
    }

    public List<Customer> getCustomersToExport() {
        return customerRepository.findAllCustomersToExport();
    }
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    //todo handle exception
    public Customer getCustomerByPeselNum(String peselNum) {
        return customerRepository.findCustomerByPeselNum(peselNum).orElseThrow();
    }

    public Customer addContact(String peselNumber, CommunicationMethods contact) {
        Customer customer = customerRepository.findCustomerByPeselNum(peselNumber).orElseThrow();
        customer.setContacts(contact);
        return customerRepository.save(customer);
    }
}

