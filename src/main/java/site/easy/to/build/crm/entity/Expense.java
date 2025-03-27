package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Integer expenseId;

    @Column(name = "description")
    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères.")
    private String description;

    @Column(name = "amount")
    @Positive(message = "Amount must be positive.")
    private Double amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_expense")
    private LocalDate dateExpense;

}
