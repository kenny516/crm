package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.ExpenseRepository;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private final TicketService ticketService;
    private final LeadService leadService;
    private final CustomerService customerService;

    public Expense findById(Integer id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Expense update(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void delete(Expense expense) {
        expenseRepository.delete(expense);
    }

    ///  API

    public Map<Integer, Double> getTicketExpensesByCustomer() {
        List<Customer> customers = customerService.findAll();
        Map<Integer, Double> customExpense = new HashMap<>();
        for (Customer customer : customers) {
            List<Ticket> ticketsCust = ticketService.findCustomerTickets(customer.getCustomerId());
            Double expense = 0.0;
            for (Ticket ticket : ticketsCust) {
                if (ticket.getExpense() != null) {
                    expense += ticket.getExpense().getAmount();
                }
            }
            customExpense.put(customer.getCustomerId(), expense);
        }
        return customExpense;
    }

    public Map<Integer, Double> getLeadExpensesByCustomer() {
        List<Customer> customers = customerService.findAll();
        Map<Integer, Double> customExpense = new HashMap<>();
        for (Customer customer : customers) {
            List<Lead> leadsCust = leadService.getLeadsByCustomerId(customer.getCustomerId());
            Double expense = 0.0;
            for (Lead lead : leadsCust) {
                if (lead.getExpense() != null) {
                    expense += lead.getExpense().getAmount();
                }
            }
            customExpense.put(customer.getCustomerId(), expense);
        }
        return customExpense;
    }


    public BigDecimal getTotalTicketExpenses() {
        List<Expense> expenses = expenseRepository.findAllExpenseByTicketIsNotNull();
        return expenses.stream()
                .map(expense -> BigDecimal.valueOf(expense.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLeadExpenses() {
        List<Expense> expenses = expenseRepository.findAllExpenseByLeadIsNotNull();
        return expenses.stream()
                .map(expense -> BigDecimal.valueOf(expense.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public double getTotalExpenses(Integer customerId) {
        double totalExpenses;
        double totalLeadExpenses = expenseRepository.sumExpenseLeadByCustomerID(customerId);
        double totalTicketExpense = expenseRepository.sumExpenseTicketByCustomerID(customerId);
        totalExpenses = totalLeadExpenses + totalTicketExpense;
        return totalExpenses;
    }


}
