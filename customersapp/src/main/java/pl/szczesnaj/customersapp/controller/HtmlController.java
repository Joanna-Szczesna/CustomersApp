/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.szczesnaj.customersapp.model.CommunicationMethods;
import pl.szczesnaj.customersapp.model.Customer;
import pl.szczesnaj.customersapp.service.CustomerService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
class HtmlController {
    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/welcome")
    public String displayCustomers(Model model) {

        List<Customer> customers = customerService.getCustomers().stream().peek(c -> {
            if (c.getContacts() == null) {
                c.setContacts(new CommunicationMethods());
            }
        }).collect(Collectors.toList());
        model.addAttribute("customers", customers);
        return "index";
    }
}
