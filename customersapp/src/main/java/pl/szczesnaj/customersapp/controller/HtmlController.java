/*
 * Copyright (c) 2023 Joanna Szczesna
 * All rights reserved
 */

package pl.szczesnaj.customersapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pl.szczesnaj.customersapp.model.CommunicationMethods;
import pl.szczesnaj.customersapp.model.Customer;
import pl.szczesnaj.customersapp.service.CustomerService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
class HtmlController {
    @Autowired
    private CustomerService customerService;

    @GetMapping(value = {"/welcome", "/welcome/{pageNumber}"})
    public String displayCustomers(Model model,
                                   @PathVariable(value = "pageNumber", required = false) Integer currentPage,
                                   @RequestParam(defaultValue = "ASC") Sort.Direction sort) {
        int pageNumber = currentPage != null && currentPage >= 0 ? currentPage : 0;

        Page<Customer> page = customerService.getCustomers(pageNumber, sort);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Customer> customers = page.getContent().stream().peek(c -> {
            if (c.getContacts() == null) {
                c.setContacts(new CommunicationMethods());
            }
        }).collect(Collectors.toList());

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages-1);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("customers", customers);

        return "index";
    }
}
