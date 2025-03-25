package site.easy.to.build.crm.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.DTO.DashboardDTO;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.customer.CustomerService;

import java.math.BigDecimal;
import java.util.Map;

// Data for chart

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AnalyticsRestController {
    
    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final CustomerService customerService;


    @GetMapping("/dashboard")
    public DashboardDTO getDashboardData() {
        Map<String, Double> ticketExpensesByCustomer =expenseService.getTicketExpensesByCustomer();
        Map<String, Double> leadExpensesByCustomer = expenseService.getLeadExpensesByCustomer();
        Map<String, Double> budgetsByCustomer = budgetService.getBudgetsByCustomer();
        Double totalTicketExpenses = expenseService.getTotalTicketExpenses().doubleValue();
        Double totalLeadExpenses = expenseService.getTotalLeadExpenses().doubleValue();
        Double totalBudgets = budgetService.getTotalBudget().doubleValue();

        return new DashboardDTO(ticketExpensesByCustomer, leadExpensesByCustomer, budgetsByCustomer,
                totalTicketExpenses, totalLeadExpenses, totalBudgets);
    }


    @GetMapping("/expenses/tickets/by-customer")
    public Map<String, Double> getTicketExpensesByCustomer() {
        return expenseService.getTicketExpensesByCustomer();
    }

    @GetMapping("/expenses/leads/by-customer")
    public Map<String, Double> getLeadExpensesByCustomer() {
        return expenseService.getLeadExpensesByCustomer();
    }

    @GetMapping("/budgets/by-customer")
    public Map<String, Double> getBudgetsByCustomer() {
        return budgetService.getBudgetsByCustomer();
    }

    @GetMapping("/expenses/tickets/total")
    public BigDecimal getTotalTicketExpenses() {
        return expenseService.getTotalTicketExpenses();
    }

    @GetMapping("/expenses/leads/total")
    public BigDecimal getTotalLeadExpenses() {
        return expenseService.getTotalLeadExpenses();
    }

    @GetMapping("/budgets/total")
    public BigDecimal getTotalBudget() {
        return budgetService.getTotalBudget();
    }
}