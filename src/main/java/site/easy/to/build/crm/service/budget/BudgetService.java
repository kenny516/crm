package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.DTO.BudgetDTO;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Parameter;
import site.easy.to.build.crm.repository.BudgetRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class BudgetService {
    private BudgetRepository budgetRepository;
    private ParameterService parameterService;

    public Budget findById(Integer id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public List<Budget> findBudgetsByCustomerId(int customerId) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        return budgetRepository.findBudgetByCustomer(customer);
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget update(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void delete(Budget budget) {
        budgetRepository.delete(budget);
    }


    public List<BudgetDTO> getBudgetsAfterExpense(Integer customerId) {
        List<Object[]> rawResults = budgetRepository.getBudgetsAfterExpenseRaw(customerId);
        List<BudgetDTO> budgetDTOS = rawResults.stream().map(obj -> new BudgetDTO(
                (Integer) obj[0],
                (String) obj[1],
                obj[2] != null ? ((BigDecimal) obj[2]).doubleValue() : 0.0,
                obj[3] != null ? ((BigDecimal) obj[3]).doubleValue() : 0.0,
                obj[4] != null ? ((java.sql.Date) obj[4]).toLocalDate() : null,
                obj[5] != null ? ((java.sql.Date) obj[5]).toLocalDate() : null,
                (Integer) obj[6]
        )).toList();
        return setStatus(budgetDTOS);
    }


    public List<BudgetDTO> setStatus(List<BudgetDTO> budgets) {
        Parameter parameter = parameterService.findThresholdAlert();
        for (BudgetDTO budget : budgets) {
            double threshold = budget.getInitialAmount() * parameter.getParameterValue() / 100;
            if (budget.getCurrentAmount() <= threshold) {
                budget.setStatus("Alerte budget a " + parameter.getParameterValue() + " %");
            } else {
                budget.setStatus("Budget normal");
            }
        }
        return budgets;
    }


}
