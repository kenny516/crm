package site.easy.to.build.crm.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardDTO {

    public Map<String, Double> ticketExpensesByCustomer;
    public Map<String,Double> leadExpensesByCustomer;
    public Map<String,Double> budgetByCustomer;

    public Double totalTicketExpenses;
    public Double totalLeadExpenses;
    public Double totalBudget;

}
