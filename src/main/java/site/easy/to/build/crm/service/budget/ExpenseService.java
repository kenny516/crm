package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.repository.ExpenseRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;

    public Expense findById(Integer id){
        return expenseRepository.findById(id).orElse(null);
    }

    public List<Expense> findAll(){
        return expenseRepository.findAll();
    }

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }
    public Expense update(Expense expense) {
        return expenseRepository.save(expense);
    }
    public void delete(Expense expense) {
        expenseRepository.delete(expense);
    }
}
