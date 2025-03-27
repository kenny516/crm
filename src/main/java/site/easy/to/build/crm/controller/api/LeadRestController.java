package site.easy.to.build.crm.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.lead.LeadService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/lead")
public class LeadRestController {
    private LeadService leadService;
    private ExpenseService expenseService;


    @GetMapping("")
    public ResponseEntity<List<Lead>> getLeadALL() {
        return ResponseEntity.ok(leadService.findAll());
    }
    @DeleteMapping("")
    public ResponseEntity<Void> deleteLead(@RequestParam("leadId") Integer leadId) {
        Lead lead = leadService.findByLeadId(leadId);
        if (lead == null) {
            return ResponseEntity.notFound().build();
        } else {
            leadService.delete(lead);
        }
        if (lead.getExpense()==null) {
            return ResponseEntity.notFound().build();
        }
        Expense expense = expenseService.findById(lead.getExpense().getExpenseId());
        if (expense == null) {
            return ResponseEntity.ok().build();
        } else {
            expenseService.delete(expense);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    public ResponseEntity<Lead> updateLead(@RequestBody Lead lead) {
        Lead leadVerif = leadService.findByLeadId(lead.getLeadId());
        if (leadVerif == null) {
            return ResponseEntity.notFound().build();
        }
        leadService.save(lead);
        return ResponseEntity.ok(lead);
    }

}
