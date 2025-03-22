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
    private final UserService userService;
    private final AuthenticationUtils authenticationUtils;
    private final BudgetService budgetService;



    @GetMapping("/create")
    public String showCreateForm(@RequestParam(required = false) Integer leadId,
            @RequestParam(required = false) Integer ticketId,
            Model model) {

        List<BudgetDTO> budgetDTOS = new ArrayList<>();
        BudgetDTO budgetDTOGlobal = new BudgetDTO();
        if (leadId != null) {
            Lead lead = leadService.findByLeadId(leadId);
            if (lead == null) {
                return "error/not-found";
            }
            budgetDTOS = budgetService.getBudgetsAfterExpense(lead.getCustomer().getCustomerId());
            budgetDTOGlobal = budgetService.getBudgetDTOGlobal(budgetDTOS);
            model.addAttribute("budgetDTOGlobal", budgetDTOGlobal);
            model.addAttribute("budgetDTOS", budgetDTOS);
            model.addAttribute("leadId", leadId);
        } else if (ticketId != null) {
            Ticket ticket = ticketService.findByTicketId(ticketId);
            if (ticket == null) {
                return "error/not-found";
            }
            budgetDTOS = budgetService.getBudgetsAfterExpense(ticket.getCustomer().getCustomerId());
            budgetDTOGlobal = budgetService.getBudgetDTOGlobal(budgetDTOS);
            model.addAttribute("budgetDTOGlobal", budgetDTOGlobal);
            model.addAttribute("budgetDTOS", budgetDTOS);
            model.addAttribute("ticketId", ticketId);
        } else {
            return "error/400";
        }

        model.addAttribute("expense", new Expense());
        return "expense/create-expense";
    }

    @PostMapping("/create")
    public String createExpense(@ModelAttribute("expense") @Validated Expense expense,
            BindingResult bindingResult,
            @RequestParam(required = false) Integer leadId,
            @RequestParam(required = false) Integer ticketId,
            Model model) {

        if (bindingResult.hasErrors()) {
            if (leadId != null) {
                model.addAttribute("leadId", leadId);
            }
            if (ticketId != null) {
                model.addAttribute("ticketId", ticketId);
            }
            return "expense/create-expense";
        }

        Expense savedExpense = expenseService.save(expense);

        // Mettre à jour le lead ou le ticket selon le contexte
        if (leadId != null) {
            Lead lead = leadService.findByLeadId(leadId);
            if (lead == null) {
                return "error/not-found";
            }
            lead.setExpense(savedExpense);
            leadService.save(lead);
            return "redirect:/employee/lead/show/" + leadId;
        } else if (ticketId != null) {
            Ticket ticket = ticketService.findByTicketId(ticketId);
            if (ticket == null) {
                return "error/not-found";
            }
            ticket.setExpense(savedExpense);
            ticketService.save(ticket);
            return "redirect:/employee/ticket/show-ticket/" + ticketId;
        }

        return "error/400";
    }
}
