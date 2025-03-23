package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Expense;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    @Query(value = """
                    SELECT
                        expense.*
                        FROM trigger_ticket
                        JOIN expense ON expense.expense_id = trigger_ticket.expense_id
                    WHERE trigger_ticket.expense_id IS NOT NULL
            """, nativeQuery = true)
    List<Expense> findAllByTicketIsNotNull();

    @Query(value = """
                    SELECT
                        expense.*
                        FROM trigger_lead
                        JOIN expense ON expense.expense_id = trigger_lead.expense_id
                    WHERE trigger_lead.expense_id IS NOT NULL
            """, nativeQuery = true)
    List<Expense> findAllByLeadIsNotNull();

}
