package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Customer;
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


    @Query("""
            SELECT SUM(e.amount)
            FROM Ticket t
            JOIN t.expense e
            WHERE t.customer.customerId = :customerId
            """)
    Double sumExpenseTicketByCustomerID(@Param("customerId") Integer customerId);

    @Query("""
            SELECT SUM(e.amount)
            FROM Lead l
            JOIN l.expense e
            WHERE l.customer.customerId = :customerId
            """)
    Double sumExpenseLeadByCustomerID(@Param("customerId") Integer customerId);



}
