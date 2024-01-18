/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

@RestController
@RequiredArgsConstructor
class CustomerController {
    private final CustomerService customerService;

    @GetMapping(value = "/customers")
    public Page<Customer> getCustomers(@RequestParam(required = false) Integer page, Sort.Direction sort) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        return customerService.getCustomers(pageNumber, sort);
    }

    @GetMapping(value = "/customers/{peselNum}")
    public Customer getCustomerByPeselNum(@PathVariable String peselNum) {
        return customerService.getCustomerByPeselNum(peselNum);
    }

    @PostMapping(value = "/customers")
    public ResponseEntity<Object> addCustomer(@RequestBody @Valid Customer user) {
        Customer customer = customerService.addCustomer(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{peselNum}")
                .buildAndExpand(customer.getPeselNumber())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping(value = "/customers/{peselNum}/methods")
    public ResponseEntity<Customer> addCustomerWithContacts(@PathVariable String peselNum,
                                                          @RequestBody  @Valid CommunicationMethods contact) {
         return ResponseEntity.of(customerService.addContact(peselNum, contact));

    }

    @GetMapping(value = "/customers/export")
    public void exportToCSV(HttpServletResponse response) throws IOException {
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
    }
}
