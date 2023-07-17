package pl.szczesnaj.customersapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.szczesnaj.customersapp.service.CustomerService;

@Controller
class HtmlController {
    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/welcome")
    public String displayCustomers(Model model) {
        model.addAttribute("customers", customerService.getCustomers());
        return "index";
    }
}
