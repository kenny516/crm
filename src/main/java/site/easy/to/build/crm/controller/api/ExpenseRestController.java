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

    @GetMapping("/ticket")
    public ResponseEntity<List<Ticket>> getTicketALL() {
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/lead")
    public ResponseEntity<List<Lead>> getLeadALL() {
        return ResponseEntity.ok(leadService.findAll());
    }


    @DeleteMapping("/delete-ticket")
    public ResponseEntity<Void> deleteTicket(@RequestParam("ticketId") Integer ticketId) {
        Ticket ticket = ticketService.findByTicketId(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        } else {
            ticketService.delete(ticket);
        }

        Expense expense = expenseService.findById(ticket.getExpense().getExpenseId());
        if (expense == null) {
            return ResponseEntity.notFound().build();
        } else {
            expenseService.delete(expense);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-lead")
    public ResponseEntity<Void> deleteLead(@RequestParam("leadId") Integer leadId) {
        Lead lead = leadService.findByLeadId(leadId);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        } else {
            leadService.delete(lead);
        }
        Expense expense = expenseService.findById(lead.getExpense().getExpenseId());
        if (expense == null) {
            return ResponseEntity.notFound().build();
        } else {
            expenseService.delete(expense);
        }
        return ResponseEntity.ok().build();
    }

    // EDIT AMOUNT (Please send the object (*v*))
    @PutMapping("/update-ticket")
    public ResponseEntity<Ticket> updateAmountTicket(@RequestBody Ticket ticket) {
        Ticket ticketVerif = ticketService.findByTicketId(ticket.getTicketId());
        if (ticketVerif == null) {
            return ResponseEntity.notFound().build();
        }
        ticketService.save(ticket);

        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/update-lead")
    public ResponseEntity<Lead> updateLead(@RequestBody Lead lead) {
        Lead leadVerif = leadService.findByLeadId(lead.getLeadId());
        if (leadVerif == null) {
            return ResponseEntity.notFound().build();
        }
        leadService.save(lead);
        return ResponseEntity.ok(lead);
    }

    @PutMapping("/update-expense")
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