package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.util.DatabaseResetService;

@AllArgsConstructor
@Controller
@RequestMapping()
public class DatabaseResetController {
    private final DatabaseResetService databaseResetService;

    @GetMapping("/database-reset")
    public String resetDatabase() {
        databaseResetService.resetDatabase();
        return "login";
    }
}
