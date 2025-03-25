package site.easy.to.build.crm.util;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.easy.to.build.crm.DTO.BudgetDtoCsv;
import site.easy.to.build.crm.DTO.CustomerDtoCsv;
import site.easy.to.build.crm.DTO.TicketLeadDtoCsv;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.user.UserService;

import java.sql.SQLDataException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@Service
public class DatabaseCustomUtil {

    private JdbcTemplate jdbcTemplate;
    private final Faker faker = new Faker();
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;
    // service
    private UserService userService;

    @Transactional
    public void resetDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        // Liste des tables à supprimer
        List<String> tables = List.of(
                "contract_settings",
                "email_template",
                "employee",
                "file",
                "google_drive_file",
                "lead_action",
                "lead_settings",
                "ticket_settings",
                "trigger_lead",
                "trigger_ticket",
                "trigger_contract",
                "customer",
                "customer_login_info",
                "expense",
                "budget");
        tables.forEach(table -> {
            jdbcTemplate.execute("TRUNCATE TABLE " + table);
        });
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }


    ////////////////// CSV IMPORTATION
    ////////////////// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<CustomerLoginInfo> buildCustomer(List<CustomerDtoCsv> customerDtoCsvs) {
        User admin = userService.findFirst();
        List<CustomerLoginInfo> customerLoginInfos = new ArrayList<>();
        for (CustomerDtoCsv customerDtoCsv : customerDtoCsvs) {
            Customer customer = new Customer();
            customer.setName(customerDtoCsv.getName());
            customer.setEmail(customerDtoCsv.getEmail());
            customer.setPosition(faker.job().position());
            customer.setCountry(faker.address().country());
            customer.setCity(faker.address().city());
            customer.setUser(admin);

            CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
            customerLoginInfo.setEmail(customerDtoCsv.getEmail());
            customerLoginInfo.setPasswordSet(true);
            String hashPassword = passwordEncoder.encode("2004");
            customerLoginInfo.setPassword(hashPassword);
            String token = EmailTokenUtils.generateToken();
            customerLoginInfo.setToken(token);
            customerLoginInfo.setCustomer(customer);

            customerLoginInfos.add(customerLoginInfo);
        }
        return customerLoginInfos;
    }

    public List<Budget> buildBudget(List<BudgetDtoCsv> budgetDtoCsvs, HashMap<String, Integer> mapCustomer,
            StringBuilder errorMessage) {
        List<Budget> budgets = new ArrayList<>();
        List<String> errors = new ArrayList<>(); // Collect errors here

        for (int i = 0; i < budgetDtoCsvs.size(); i++) {
            BudgetDtoCsv budgetDtoCsv = budgetDtoCsvs.get(i);
            Budget budget = new Budget();
            Integer customerId = mapCustomer.get(budgetDtoCsv.getCustomerEmail());

            if (customerId == null) {
                errors.add(String.format("Row %d: Customer email '%s' not found for budget.", i + 2,
                        budgetDtoCsv.getCustomerEmail()));
            } else {
                Customer customer = new Customer();
                customer.setCustomerId(customerId);

                budget.setTitle(faker.book().title());
                budget.setAmount(budgetDtoCsv.getBudget());
                budget.setStartDate(LocalDate.now());
                budget.setEndDate(LocalDate.now());
                budget.setCustomer(customer);

                budgets.add(budget);
            }
        }

        if (!errors.isEmpty()) {
            errorMessage.append("<ul>");
            errors.forEach(error -> errorMessage.append("<li>").append(error).append("</li>"));
            errorMessage.append("</ul>");
        }

        return budgets;
    }

    public List<Ticket> buildTicket(List<TicketLeadDtoCsv> ticketLeadDtoCsvs, HashMap<String, Integer> mapCustomer,
            StringBuilder errorMessage) {
        User admin = userService.findFirst();
        List<Ticket> tickets = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < ticketLeadDtoCsvs.size(); i++) {
            TicketLeadDtoCsv ticketLeadDtoCsv = ticketLeadDtoCsvs.get(i);
            if (ticketLeadDtoCsv.getType().equalsIgnoreCase("ticket")
                    || ticketLeadDtoCsv.getType().equalsIgnoreCase("tickets")) {
                Ticket ticket = new Ticket();
                Integer customerId = mapCustomer.get(ticketLeadDtoCsv.getCustomerEmail());

                if (customerId == null) {
                    errors.add(String.format("Row %d: Customer email '%s' not found for ticketLeadCsv.", i + 2,
                            ticketLeadDtoCsv.getCustomerEmail()));
                } else {
                    Expense expense = new Expense();
                    expense.setAmount(ticketLeadDtoCsv.getExpense());
                    expense.setDateExpense(LocalDate.now());
                    expense.setDescription(faker.lorem().sentence());

                    Customer customer = new Customer();
                    customer.setCustomerId(customerId);

                    ticket.setSubject(ticketLeadDtoCsv.getSubjectOrName());
                    ticket.setDescription(faker.lorem().sentence());
                    ticket.setStatus(ticketLeadDtoCsv.getStatus());
                    ticket.setPriority("critical");
                    ticket.setCustomer(customer);
                    ticket.setManager(admin);
                    ticket.setEmployee(admin);
                    ticket.setCreatedAt(LocalDateTime.now());
                    ticket.setExpense(expense);

                    tickets.add(ticket);
                }
            }
        }

        if (!errors.isEmpty()) {
            errorMessage.append("<ul>");
            errors.forEach(error -> errorMessage.append("<li>").append(error).append("</li>"));
            errorMessage.append("</ul>");
        }

        return tickets;
    }

    public List<Lead> buildLead(List<TicketLeadDtoCsv> ticketLeadDtoCsvs, HashMap<String, Integer> mapCustomer,
            StringBuilder errorMessage) {
        User admin = userService.findFirst();
        List<Lead> leads = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < ticketLeadDtoCsvs.size(); i++) {
            TicketLeadDtoCsv ticketLeadDtoCsv = ticketLeadDtoCsvs.get(i);
            if (ticketLeadDtoCsv.getType().equalsIgnoreCase("lead")
                    || ticketLeadDtoCsv.getType().equalsIgnoreCase("leads")) {
                Lead lead = new Lead();
                Integer customerId = mapCustomer.get(ticketLeadDtoCsv.getCustomerEmail());

                if (customerId == null) {
                    errors.add(String.format("Row %d: Customer email '%s' not found for ticketLeadCsv.", i + 2,
                            ticketLeadDtoCsv.getCustomerEmail()));
                } else {
                    Expense expense = new Expense();
                    expense.setAmount(ticketLeadDtoCsv.getExpense());
                    expense.setDateExpense(LocalDate.now());
                    expense.setDescription(faker.lorem().sentence());

                    Customer customer = new Customer();
                    customer.setCustomerId(customerId);

                    lead.setCustomer(customer);
                    lead.setManager(admin);
                    lead.setName(ticketLeadDtoCsv.getSubjectOrName());
                    lead.setEmployee(admin);
                    lead.setStatus(ticketLeadDtoCsv.getStatus());
                    lead.setCreatedAt(LocalDateTime.now());
                    lead.setExpense(expense);

                    leads.add(lead);
                }
            }
        }

        if (!errors.isEmpty()) {
            errorMessage.append("<ul>");
            errors.forEach(error -> errorMessage.append("<li>").append(error).append("</li>"));
            errorMessage.append("</ul>");
        }
        return leads;
    }

    @Transactional(rollbackFor = SQLDataException.class)
    public void importCsvAndSave(List<BudgetDtoCsv> budgetDtoCsvs, List<TicketLeadDtoCsv> ticketLeadDtoCsvs,
            List<CustomerDtoCsv> customerDtoCsvs) throws SQLDataException {
        StringBuilder errorMessage = new StringBuilder();
        HashMap<String, Integer> mapCustomer = new HashMap<>();

        // 1. Sauvegarde des clients
        List<CustomerLoginInfo> customerLoginInfos = buildCustomer(customerDtoCsvs);
        for (CustomerLoginInfo customerLoginInfo : customerLoginInfos) {
            entityManager.persist(customerLoginInfo.getCustomer()); // Persist the customer first
            entityManager.persist(customerLoginInfo);
            mapCustomer.put(customerLoginInfo.getEmail(), customerLoginInfo.getCustomer().getCustomerId());
        }

        // 2. Sauvegarde des budgets
        List<Budget> budgets = buildBudget(budgetDtoCsvs, mapCustomer, errorMessage);
        for (Budget budget : budgets) {
            entityManager.persist(budget);
        }

        // 3. Sauvegarde des tickets
        List<Ticket> tickets = buildTicket(ticketLeadDtoCsvs, mapCustomer, errorMessage);
        for (Ticket ticket : tickets) {
            entityManager.persist(ticket.getExpense()); // Persist the expense first
            entityManager.persist(ticket);
        }

        // 4. Sauvegarde des leads
        List<Lead> leads = buildLead(ticketLeadDtoCsvs, mapCustomer, errorMessage);
        for (Lead lead : leads) {
            entityManager.persist(lead.getExpense()); // Persist the expense first
            entityManager.persist(lead);
        }
        if (!errorMessage.isEmpty()) {
            throw new SQLDataException(errorMessage.toString());
        }
    }
}
