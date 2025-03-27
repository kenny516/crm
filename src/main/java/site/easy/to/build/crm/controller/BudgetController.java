package site.easy.to.build.crm.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.util.AuthenticationUtils;

import lombok.AllArgsConstructor;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.util.List;

@Controller
@RequestMapping("/budget")
@AllArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final CustomerService customerService;

    @GetMapping("/create/{customerId}")
    public String showCreateBudgetForm(@PathVariable("customerId") int customerId, Model model) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return "error/not-found";
        }

        Budget budget = new Budget();
        budget.setCustomer(customer);
        model.addAttribute("budget", budget);
        return "budget/create-budget";
    }

    @PostMapping("/create-budget")
    public String createBudget(@Valid @ModelAttribute("budget") Budget budget, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("budget", budget);
            return "budget/create-budget";
        }
        try {
            budgetService.save(budget);
            return "redirect:/employee/customer/" + budget.getCustomer().getCustomerId();
        } catch (Exception e) {
            return "error/500";
        }
    }

    @GetMapping("/list/{customerId}")
    public String listBudgets(@PathVariable("customerId") int customerId, Model model) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            return "error/not-found";
        }
        List<Budget> budgets = budgetService.findBudgetsByCustomerId(customerId);
        model.addAttribute("budgets", budgets);
        model.addAttribute("customer", customer);

        return "budget/budget-list";
    }
}