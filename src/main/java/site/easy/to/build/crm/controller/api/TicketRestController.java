package site.easy.to.build.crm.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.ticket.TicketService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/ticket")
public class TicketRestController {
    private TicketService ticketService;
    private ExpenseService expenseService;

    @GetMapping("")
    public ResponseEntity<List<Ticket>> getTicketALL() {
        List<Ticket> tickets = ticketService.findAll();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteTicket(@RequestParam("ticketId") Integer ticketId) {
        Ticket ticket = ticketService.findByTicketId(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        } else {
            ticketService.delete(ticket);
        }
        if (ticket.getExpense()== null) {
            return ResponseEntity.notFound().build();
        }

        Expense expense = expenseService.findById(ticket.getExpense().getExpenseId());
        if (expense == null) {
            return ResponseEntity.ok().build();
        } else {
            expenseService.delete(expense);
        }
        return ResponseEntity.ok().build();
    }
    @PutMapping("")
    public ResponseEntity<Ticket> updateAmountTicket(@RequestBody Ticket ticket) {
        Ticket ticketVerif = ticketService.findByTicketId(ticket.getTicketId());
        if (ticketVerif == null) {
            return ResponseEntity.notFound().build();
        }
        ticketService.save(ticket);

        return ResponseEntity.ok(ticket);
    }

}
