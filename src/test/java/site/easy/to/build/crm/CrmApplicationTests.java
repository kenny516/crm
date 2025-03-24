package site.easy.to.build.crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.customer.CustomerService;

import java.util.List;

@SpringBootTest
class CrmApplicationTests {

	@Autowired
	BudgetService budgetService;
	@Autowired
	CustomerService customerService;
	@Autowired
	ExpenseService expenseService;

	@Test
	void contextLoads() {
		double sumBudget = budgetService.getTotalBudgetByCustomer(43);
		double sumExpense = expenseService.getTotalExpenses(43);
		System.out.println("budget "+sumBudget);
		System.out.println("expense "+sumExpense);
	}

}
