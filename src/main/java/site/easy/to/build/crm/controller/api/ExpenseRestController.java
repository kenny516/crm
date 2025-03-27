package site.easy.to.build.crm.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.budget.ExpenseService;

import java.util.List;

@RestController
@RequestMapping("/api/expense")
@AllArgsConstructor
public class ExpenseRestController {

    private final ExpenseService expenseService;
    private final LeadService leadService;
    private final TicketService ticketService;


    // GET DATA
    @GetMapping("")
    public ResponseEntity<List<Expense>> getExpenses() {
        return ResponseEntity.ok(expenseService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<Expense> updateExpense(@RequestBody Expense expense) {
        Expense expenseVerif = expenseService.findById(expense.getExpenseId());
        if (expenseVerif == null) {
            return ResponseEntity.notFound().build();
        }
        expenseService.save(expense);
        return ResponseEntity.ok(expense);
    }

    // DELETE EXPENSE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Integer id) {
        Expense expense = expenseService.findById(id);
        if (expense == null) {
            return ResponseEntity.notFound().build();
        }
        expenseService.delete(expense);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> GetExpenseById(@PathVariable Integer id) {
        Expense expense = expenseService.findById(id);
        return ResponseEntity.ok(expense);
    }

}