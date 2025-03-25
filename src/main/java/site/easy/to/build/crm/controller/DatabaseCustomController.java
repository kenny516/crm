package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.easy.to.build.crm.util.DatabaseCustomUtil;

import java.sql.SQLDataException;
import java.util.Map;

@AllArgsConstructor
@Controller
@RequestMapping("/database")
public class DatabaseCustomController {
    private final DatabaseCustomUtil databaseCustomUtil;

    @GetMapping("/generate")
    public String showGenerateForm() {
        return "data-modifier/generate/generate-data";
    }

    @PostMapping("/generate")
    public String generateData(
            @RequestParam int customerCount,
            @RequestParam int budgetPerCustomer,
            @RequestParam int ticketPerCustomer,
            @RequestParam int leadPerCustomer) throws SQLDataException {

        Map<String, Integer> results = databaseCustomUtil.generateAndSaveRandomData(
                customerCount, budgetPerCustomer, ticketPerCustomer, leadPerCustomer);

        return "redirect:/database/generate?success=true";
    }

    @GetMapping("/reset")
    public String resetDatabase() {
        databaseCustomUtil.resetDatabase();
        System.out.println("Resetting database...");
        return "redirect:/";
    }
}
