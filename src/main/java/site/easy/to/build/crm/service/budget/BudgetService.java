package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.DTO.BudgetDTO;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Parameter;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.service.customer.CustomerService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private ParameterService parameterService;
    private CustomerService customerService;
    private ExpenseService expenseService;


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
                (Integer) obj[6])).toList();
        return setStatus(budgetDTOS);
    }

    public List<BudgetDTO> setStatus(List<BudgetDTO> budgets) {
        Parameter parameter = parameterService.findThresholdAlert();
        for (BudgetDTO budget : budgets) {
            double threshold = budget.getInitialAmount() * parameter.getParameterValue() / 100;
            if (budget.getInitialAmount() - budget.getCurrentAmount() >= threshold) {
                budget.setStatus("Alerte budget a " + parameter.getParameterValue() + " % "
                        + "Budget initial : " + budget.getInitialAmount() + "\n" +
                        "Budget Actuel :  " + budget.getCurrentAmount() + "\n");
            } else {
                budget.setStatus("Budget normal");
            }
        }
        return budgets;
    }

//    public Double cumulBudget(Integer customerId) {
//        return ((BigDecimal) budgetRepository.getBudgetsAfterExpenseRawGlobal(customerId)).doubleValue();
//    }

    public BudgetDTO getBudgetGlobal(Integer customerId) {
        BudgetDTO budgetDTO = new BudgetDTO();
        double sumBudget = getTotalBudgetByCustomer(customerId);
        double sumExpense = expenseService.getTotalExpenses(customerId);
        budgetDTO.setBudgetId(customerId);
        budgetDTO.setInitialAmount(sumBudget);
        budgetDTO.setCurrentAmount(sumBudget - sumExpense);
        return budgetDTO;
    }

    public BudgetDTO getBudgetDTOGlobal(Integer customerId) {
        Parameter parameter = parameterService.findThresholdAlert();
        BudgetDTO budgetDTO = getBudgetGlobal(customerId);
        double threshold = budgetDTO.getInitialAmount() * parameter.getParameterValue() / 100;
        if (budgetDTO.getInitialAmount() - budgetDTO.getCurrentAmount() >= threshold) {
            budgetDTO.setStatus("Alerte budget au plafon " + parameter.getParameterValue() + " % \n " +
                    "Budget initial : " + budgetDTO.getInitialAmount() + "\n" +
                    "Budget Actuel :  " + budgetDTO.getCurrentAmount() + "\n");
        } else {
            budgetDTO.setStatus("Budget normal");
        }
        return budgetDTO;
    }

    // API SERVICE

    public List<BudgetDTO> getBudgetDTOSGlobalGrouped() {
        List<Customer> customers = customerService.findAll();
        List<BudgetDTO> budgetDTOS = new ArrayList<>();
        for (Customer customer : customers) {
            budgetDTOS.add(getBudgetDTOGlobal(customer.getCustomerId()));
        }
        return budgetDTOS;
    }

    public Map<Integer, Double> getBudgetsByCustomer() {
        List<Budget> budgets = budgetRepository.findAll();
        return budgets.stream()
                .collect(Collectors.groupingBy(
                        budget -> budget.getCustomer().getCustomerId(),
                        Collectors.summingDouble(Budget::getAmount) // Additionner les montants
                ));
    }

    public BigDecimal getTotalBudget() {
        List<Budget> budgets = budgetRepository.findAll();
        return budgets.stream()
                .map(budget -> BigDecimal.valueOf(budget.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    ///
    public double getTotalBudgetByCustomer(Integer customerId) {
        return budgetRepository.getTotalBudgetByCustomer(customerId);
    }
}
