package site.easy.to.build.crm.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Parameter;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.budget.ParameterService;

import java.util.List;

@RestController
@RequestMapping("/api/management")
@AllArgsConstructor
public class BudgetManagementRestController {

    private final BudgetService budgetService;
    private final ParameterService parameterService;


    // Get
    @GetMapping("/budget")
    public ResponseEntity<List<Budget>> getBudgets() {
        List<Budget> budgets = budgetService.findAll();
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }

    @GetMapping("/budget/customer/{customerId}")
    public ResponseEntity<List<Budget>> getBudget(@PathVariable Integer customerId) {
        List<Budget> budget = budgetService.findBudgetsByCustomerId(customerId);
        return new ResponseEntity<>(budget, HttpStatus.OK);
    }

    // Suppression d'un budget
    @DeleteMapping("/budget/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer id) {
        Budget budget = budgetService.findById(id);
        if (budget == null) {
            return ResponseEntity.notFound().build();
        }
        budgetService.delete(budget);
        return ResponseEntity.ok().build();
    }

///  Parameter api
    @PutMapping("/threshold")
    public ResponseEntity<Parameter> updateThresholdAlert(@RequestParam Double value) {
        Parameter parameter = parameterService.updateThresholdAlert(value);
        return ResponseEntity.ok(parameter);
    }
    // Obtention du seuil d'alerte actuel
    @GetMapping("/threshold")
    public ResponseEntity<Parameter> getThresholdAlert() {
        return ResponseEntity.ok(parameterService.findThresholdAlert());
    }
}