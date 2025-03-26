package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
     Integer budgetId;

    @Column(name = "title")
     String title;

    @Column(name = "amount")
    @Positive(message = "Le montant doit être supérieur à 0.")
     Double amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
     LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
     LocalDate endDate;

    @ManyToOne(optional = false )
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Le client associé est obligatoire.")
    Customer customer;

}
