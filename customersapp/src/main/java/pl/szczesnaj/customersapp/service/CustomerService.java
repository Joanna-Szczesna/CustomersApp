/*
 * Copyright (c) 2024 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.service;

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

    public Optional<Customer> addCustomer(Customer customer) {
        Optional<Customer> existedCustomer = getCustomerByPeselNum(customer.getPeselNumber());
        if(existedCustomer.isPresent()){
            return Optional.empty();
        }
        return Optional.of(customerRepository.save(customer));
    }

    public Optional<Customer> getCustomerByPeselNum(String peselNum) {
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
        Optional<Customer> customerToDelete = findByPeselNum(peselNum);
        customerToDelete.ifPresent(customerRepository::delete);
    }

    private Optional<Customer> findByPeselNum(String peselNum) {
        return customerRepository.findCustomerByPeselNum(peselNum);
    }

    @Transactional
    public Optional<Customer> editCustomer(Customer customerRequest) {
        Optional<Customer> customer = customerRepository.findCustomerByPeselNum(customerRequest.getPeselNumber());
       if(customer.isEmpty()){
            return Optional.empty();
        }
        Customer customerEdited = customer.get();
        customerEdited.setName(customerRequest.getName());
        customerEdited.setSurname(customerRequest.getSurname());
        customerEdited.setPeselNumber(customerRequest.getPeselNumber());
        return Optional.of(customerEdited);
    }
}
