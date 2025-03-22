package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.easy.to.build.crm.util.DatabaseCustomUtil;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/database")
public class DatabaseCustomController {
    private final DatabaseCustomUtil databaseCustomUtil;

    @GetMapping("/import")
    public String importDatabase() {
        System.out.println("Importing database...");
        return "redirect:/";
    }

    @GetMapping("/generate")
    public String showGenerateForm() {
        return "data-modifier/generate/generate-data";
    }

    @PostMapping("/generate")
    public String generateData(@RequestParam List<String> selectedTables,
            @RequestParam int recordCount) {
        databaseCustomUtil.generateDataTable(selectedTables, recordCount);
        return "redirect:/database/generate?success=true";
    }

    @GetMapping("/reset")
    public String resetDatabase() {
        databaseCustomUtil.resetDatabase();
        System.out.println("Resetting database...");
        return "redirect:/";
    }
}
