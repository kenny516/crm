package site.easy.to.build.crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.easy.to.build.crm.DTO.BudgetDTO;
import site.easy.to.build.crm.service.budget.BudgetService;

import java.util.List;

@SpringBootTest
class CrmApplicationTests {

	@Autowired
	BudgetService budgetService;

	@Test
	void contextLoads() {

		List<BudgetDTO> budgetDTOS =  budgetService.getBudgetsAfterExpense(43);
		for (int i = 0; i < budgetDTOS.size(); i++) {
			System.out.println(budgetDTOS.get(i).getBudgetId());
			System.out.println(budgetDTOS.get(i).getTitle());
			System.out.println(budgetDTOS.get(i).getInitialAmount());
			System.out.println(budgetDTOS.get(i).getCurrentAmount());
			System.out.println(budgetDTOS.get(i).getStartDate());
			System.out.println(budgetDTOS.get(i).getEndDate());
			System.out.println(budgetDTOS.get(i).getCustomerId());

		}
	}

}
