/*
 * Copyright (c) 2024 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.io.dozer.ICsvDozerBeanWriter;
import org.supercsv.prefs.CsvPreference;
import pl.szczesnaj.customersapp.model.CommunicationMethods;
import pl.szczesnaj.customersapp.model.Customer;
import pl.szczesnaj.customersapp.service.CustomerService;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
class CustomerController {
    private final CustomerService customerService;

    @GetMapping(value = "/{peselNum}")
    public ResponseEntity<Customer> getCustomerByPeselNum(@PathVariable String peselNum) {
        Optional<Customer> customer = customerService.getCustomerByPeselNum(peselNum);
        return customer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody @Valid Customer user) {
        Optional<Customer> customer = customerService.addCustomer(user);
        if(customer.isPresent()) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{peselNum}")
                    .buildAndExpand(customer.get().getPeselNumber())
                    .toUri();

            return ResponseEntity.created(location).build();
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PostMapping(value = "/{peselNum}/methods")
    public ResponseEntity<Customer> addContactToCustomer(@PathVariable String peselNum,
                                                         @RequestBody @Valid CommunicationMethods contact) {
        return ResponseEntity.of(customerService.addContact(peselNum, contact));
    }

    @DeleteMapping(value = "/{peselNum}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String peselNum) {
        customerService.deleteCustomer(peselNum);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Customer> editCustomer(@RequestBody @Valid Customer customerRequest) {
        Optional<Customer> customer = customerService.editCustomer(customerRequest);
        return customer.map(c ->  new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<Page<Customer>> getAllCustomers(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        Page<Customer> customers = customerService.getCustomers(pageNumber, sort);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping(value = "/export")
    public ResponseEntity<Void> exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=customers_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);
        final List<Customer> customers = customerService.getCustomers();

        try (ICsvDozerBeanWriter beanWriter = new CsvDozerBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE)) {
            String[] csvHeader = {"Name", "Surname", "PESEL number",
                    "Email", "Residence Address", "Registered Address",
                    "Private Phone Number", "Business Phone Number"};
            String[] fieldMappings = {"name", "surname", "peselNumber",
                    "contacts.emailAddress", "contacts.residenceAddress", "contacts.registeredAddress",
                    "contacts.privatePhoneNumber", "contacts.businessPhoneNumber"};

            beanWriter.configureBeanMapping(Customer.class, fieldMappings);

            beanWriter.writeHeader(csvHeader);
            for (Customer c : customers) {
                beanWriter.write(c);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
