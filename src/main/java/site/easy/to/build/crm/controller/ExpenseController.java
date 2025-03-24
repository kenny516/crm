package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.DTO.BudgetDTO;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/employee/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final LeadService leadService;
    private final TicketService ticketService;
    private final BudgetService budgetService;



    @GetMapping("/create")
    public String showCreateForm(@RequestParam(required = false) Integer leadId,
            @RequestParam(required = false) Integer ticketId,
            Model model) {

        List<BudgetDTO> budgetDTOS = new ArrayList<>();
        BudgetDTO budgetDTOGlobal = new BudgetDTO();
        Expense expense = new Expense();
        if (leadId != null) {
            Lead lead = leadService.findByLeadId(leadId);
            if (lead == null) {
                return "error/not-found";
            }
            if (lead.getExpense() != null) {
                expense = lead.getExpense();
            }
            budgetDTOS = budgetService.getBudgetsAfterExpense(lead.getCustomer().getCustomerId());
            budgetDTOGlobal = budgetService.getBudgetDTOGlobal(lead.getCustomer().getCustomerId());
            model.addAttribute("budgetDTOGlobal", budgetDTOGlobal);
            model.addAttribute("budgetDTOS", budgetDTOS);
            model.addAttribute("leadId", leadId);
        } else if (ticketId != null) {
            Ticket ticket = ticketService.findByTicketId(ticketId);
            if (ticket == null) {
                return "error/not-found";
            }
            if (ticket.getExpense() != null) {
                expense = ticket.getExpense();
            }
            budgetDTOS = budgetService.getBudgetsAfterExpense(ticket.getCustomer().getCustomerId());
            budgetDTOGlobal = budgetService.getBudgetDTOGlobal(ticket.getCustomer().getCustomerId());
            model.addAttribute("budgetDTOGlobal", budgetDTOGlobal);
            model.addAttribute("budgetDTOS", budgetDTOS);
            model.addAttribute("ticketId", ticketId);
        } else {
            return "error/400";
        }

        model.addAttribute("expense", expense);
        return "expense/create-expense";
    }

    @PostMapping("/create")
    public String createExpense(@ModelAttribute("expense") @Validated Expense expense,
            BindingResult bindingResult,
            @RequestParam(required = false) Integer leadId,
            @RequestParam(required = false) Integer ticketId,
            Model model) {
        System.out.println("error "+bindingResult.hasErrors());

        // Mettre à jour le lead ou le ticket selon le contexte
        if (leadId != null) {
            Lead lead = leadService.findByLeadId(leadId);
            if (lead == null) {
                return "error/not-found";
            }
            expense.setCustomer(lead.getCustomer());
            Expense savedExpense = expenseService.save(expense);
            lead.setExpense(savedExpense);
            leadService.save(lead);
            return "redirect:/employee/lead/show/" + leadId;
        } else if (ticketId != null) {
            Ticket ticket = ticketService.findByTicketId(ticketId);
            if (ticket == null) {
                return "error/not-found";
            }
            expense.setCustomer(ticket.getCustomer());
            Expense savedExpense = expenseService.save(expense);
            ticket.setExpense(savedExpense);
            ticketService.save(ticket);
            return "redirect:/employee/ticket/show-ticket/" + ticketId;
        }

        return "error/400";
    }
}
