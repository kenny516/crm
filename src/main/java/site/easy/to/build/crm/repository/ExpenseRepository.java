package site.easy.to.build.crm.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.Expense;

public interface ExpenseRepository  extends JpaRepository<Expense, Integer> {
}
