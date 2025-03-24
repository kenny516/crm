package site.easy.to.build.crm.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.easy.to.build.crm.entity.Customer;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetDTO {
    private Integer budgetId;
    private String title;
    private Double initialAmount;
    private Double currentAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer customerId;
    private String status;

    public BudgetDTO(Integer budgetId, String title, Double initialAmount, Double currentAmount, LocalDate startDate, LocalDate endDate, Integer customerId) {
        this.budgetId = budgetId;
        this.title = title;
        this.initialAmount = initialAmount;
        this.currentAmount = currentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerId = customerId;
    }
}
