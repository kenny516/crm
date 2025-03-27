package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.DTO.BudgetDtoCsv;
import site.easy.to.build.crm.DTO.CustomerDtoCsv;
import site.easy.to.build.crm.DTO.TicketLeadDtoCsv;
import site.easy.to.build.crm.util.CsvUtil;
import site.easy.to.build.crm.util.DatabaseCustomUtil;

import java.io.IOException;
import java.sql.SQLDataException;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/csv")
public class CsvController {
    private final CsvUtil csvUtil;
    private final DatabaseCustomUtil databaseCustomUtil;

    @GetMapping("/import")
    public String showImportForm() {
        return "data-modifier/csv/import-csv";
    }
// process the uploaded files
    @PostMapping("/import")
    public String handleFileUpload(@RequestParam("customercsv") MultipartFile customerFile,
                                   @RequestParam("ticketleadcsv") MultipartFile ticketLeadFile,
                                   @RequestParam("budgetcsv") MultipartFile budgetFile, Model model) throws IOException {

        boolean isErrorHappens = false;
        ///  import customer
        StringBuilder erroCustomer = new StringBuilder();
        List<CustomerDtoCsv> customerDtoCsvs = csvUtil.read(CustomerDtoCsv.class,customerFile.getInputStream(),erroCustomer);
        if (!erroCustomer.isEmpty()) {
            isErrorHappens = true;
            model.addAttribute("customerError",erroCustomer.toString());
        }
        ///  import budget
        StringBuilder erroBudget = new StringBuilder();
        List<BudgetDtoCsv> budgetDtoCsvs = csvUtil.read(BudgetDtoCsv.class,budgetFile.getInputStream(),erroBudget);
        if (!erroBudget.isEmpty()) {
            isErrorHappens = true;
            model.addAttribute("budgetError",erroBudget.toString());
        }
        ///  import Ticket lead
        StringBuilder errorTicketLead = new StringBuilder();
        List<TicketLeadDtoCsv> ticketLeadDtoCsvs = csvUtil.read(TicketLeadDtoCsv.class,ticketLeadFile.getInputStream(),errorTicketLead);
        if (!errorTicketLead.isEmpty()) {
            isErrorHappens = true;
            model.addAttribute("ticketLeadError",errorTicketLead.toString());
        }

        if (isErrorHappens) {
            return "data-modifier/csv/import-csv";
        }
        try{
            databaseCustomUtil.importCsvAndSave(budgetDtoCsvs,ticketLeadDtoCsvs,customerDtoCsvs);
        }catch (SQLDataException exception){
            model.addAttribute("sqlError",exception.getMessage());
            return "data-modifier/csv/import-csv";
        }

        return "redirect:/csv/import?success=true";

    }
}
