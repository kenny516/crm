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

    public List<CustomerLoginInfo> buildCustomer(List<CustomerDtoCsv> customerDtoCsvs, StringBuilder errorMessage) {
        User admin = userService.findFirst();
        List<CustomerLoginInfo> customerLoginInfos = new ArrayList<>();
        Set<String> uniqueEmails = new HashSet<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < customerDtoCsvs.size(); i++) {
            CustomerDtoCsv customerDtoCsv = customerDtoCsvs.get(i);
            String email = customerDtoCsv.getEmail();

            if (!uniqueEmails.add(email)) {
                errors.add(String.format("CustomerCSV Row %d: Duplicate email '%s' found.", i + 2, email));
                continue;
            }

            Customer customer = new Customer();
            customer.setName(customerDtoCsv.getName());
            customer.setEmail(email);
            customer.setPosition(faker.job().position());
            customer.setCountry(faker.address().country());
            customer.setCity(faker.address().city());
            customer.setUser(admin);

            CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
            customerLoginInfo.setEmail(email);
            customerLoginInfo.setPasswordSet(true);
            String hashPassword = passwordEncoder.encode("2004");
            customerLoginInfo.setPassword(hashPassword);
            String token = EmailTokenUtils.generateToken();
            customerLoginInfo.setToken(token);
            customerLoginInfo.setCustomer(customer);

            customerLoginInfos.add(customerLoginInfo);
        }

        if (!errors.isEmpty()) {
            errorMessage.append("<ul>");
            errors.forEach(error -> errorMessage.append("<li>").append(error).append("</li>"));
            errorMessage.append("</ul>");
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
                errors.add(String.format("BudgetCsv Row %d: Customer email '%s' not found for budget.", i + 2,
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
        String[] priorities = { "low","medium","high","closed","urgent","critical" };
        for (int i = 0; i < ticketLeadDtoCsvs.size(); i++) {
            TicketLeadDtoCsv ticketLeadDtoCsv = ticketLeadDtoCsvs.get(i);
            if (ticketLeadDtoCsv.getType().equalsIgnoreCase("ticket")
                    || ticketLeadDtoCsv.getType().equalsIgnoreCase("tickets")) {
                Ticket ticket = new Ticket();
                Integer customerId = mapCustomer.get(ticketLeadDtoCsv.getCustomerEmail());

                if (customerId == null) {
                    errors.add(String.format("ticketLeadCsv Row %d: Customer email '%s' not found.", i + 2,
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
                    ticket.setPriority(priorities[ThreadLocalRandom.current().nextInt(0, priorities.length)]);
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
                    errors.add(String.format("ticketLeadCsv Row %d: Customer email '%s' not found.", i + 2,
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
        List<CustomerLoginInfo> customerLoginInfos = buildCustomer(customerDtoCsvs, errorMessage);
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

    ////////////////// GÉNÉRATION DE DONNÉES ALÉATOIRES
    ////////////////// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Génère des clients aléatoires avec Faker
     *
     * @param count Nombre de clients à générer
     * @return Liste des CustomerLoginInfo générés
     */
    public List<CustomerLoginInfo> generateRandomCustomers(int count) {
        User admin = userService.findFirst();
        List<CustomerLoginInfo> customersInfos = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String username = faker.name().username();
            String email = username.toLowerCase().replace(" ", "") + "@gmail.com";

            Customer customer = new Customer();
            customer.setName(username);
            customer.setPosition(faker.company().profession());
            customer.setEmail(email);
            customer.setUser(admin);
            customer.setCountry(faker.address().country());
            customer.setCity(faker.address().city());

            CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
            customerLoginInfo.setEmail(email);
            customerLoginInfo.setPasswordSet(true);
            String hashPassword = passwordEncoder.encode("2004");
            customerLoginInfo.setPassword(hashPassword);
            String token = EmailTokenUtils.generateToken();
            customerLoginInfo.setToken(token);
            customerLoginInfo.setCustomer(customer);

            customersInfos.add(customerLoginInfo);
        }
        return customersInfos;
    }

    /**
     * Génère des budgets aléatoires pour les clients existants
     *
     * @param customers Liste des clients
     * @param count     Nombre de budgets à générer par client
     * @return Liste des budgets générés
     */
    public List<Budget> generateRandomBudgets(List<Customer> customers, int count) {
        List<Budget> budgets = new ArrayList<>();

        for (Customer customer : customers) {
            for (int i = 0; i < count; i++) {
                Budget budget = new Budget();

                budget.setTitle(faker.commerce().department() + " Budget");
                // Montant aléatoire entre 1000 et 50000
                double amount = 1000 + (Math.random() * 49000);
                budget.setAmount((double) Math.round(amount * 100) / 100);

                LocalDate now = LocalDate.now();
                LocalDate startDate = now.minusDays(ThreadLocalRandom.current().nextInt(0, 60));
                LocalDate endDate = startDate.plusMonths(ThreadLocalRandom.current().nextInt(1, 13));

                budget.setStartDate(startDate);
                budget.setEndDate(endDate);
                budget.setCustomer(customer);

                budgets.add(budget);
            }
        }
        return budgets;
    }

    /**
     * Génère des tickets aléatoires pour les clients existants
     *
     * @param customers Liste des clients
     * @param count     Nombre de tickets à générer par client
     * @return Liste des tickets générés
     */
    public List<Ticket> generateRandomTickets(List<Customer> customers, int count) {
        User admin = userService.findFirst();
        List<Ticket> tickets = new ArrayList<>();
        String[] statuses = { "open","assigned","on-hold","in-progress","resolved","closed","reopened","pending-customer-response","escalated","archived" };
        String[] priorities = { "low","medium","high","closed","urgent","critical" };

        for (Customer customer : customers) {
            for (int i = 0; i < count; i++) {
                Ticket ticket = new Ticket();

                Expense expense = new Expense();
                // Montant aléatoire entre 50 et 2000
                double expenseAmount = 50 + (Math.random() * 1950);
                expense.setAmount((double) Math.round(expenseAmount * 100) / 100);
                expense.setDateExpense(LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(0, 30)));
                expense.setDescription(faker.lorem().sentence());

                ticket.setSubject(faker.lorem().sentence(3, 3));
                ticket.setDescription(faker.lorem().paragraph());
                ticket.setStatus(statuses[ThreadLocalRandom.current().nextInt(0, statuses.length)]);
                ticket.setPriority(priorities[ThreadLocalRandom.current().nextInt(0, priorities.length)]);
                ticket.setCustomer(customer);
                ticket.setManager(admin);
                ticket.setEmployee(admin);
                ticket.setCreatedAt(LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(0, 30)));
                ticket.setExpense(expense);

                tickets.add(ticket);
            }
        }
        return tickets;
    }

    /**
     * Génère des leads aléatoires pour les clients existants
     *
     * @param customers Liste des clients
     * @param count     Nombre de leads à générer par client
     * @return Liste des leads générés
     */
    public List<Lead> generateRandomLeads(List<Customer> customers, int count) {
        User admin = userService.findFirst();
        List<Lead> leads = new ArrayList<>();
        String[] statuses = { "meeting-to-schedule","scheduled","archived","success","assign-to-sales"};

        for (Customer customer : customers) {
            for (int i = 0; i < count; i++) {
                Lead lead = new Lead();

                Expense expense = new Expense();
                // Montant aléatoire entre 50 et 1000
                double expenseAmount = 50 + (Math.random() * 950);
                expense.setAmount((double) Math.round(expenseAmount * 100) / 100);
                expense.setDateExpense(LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(0, 30)));
                expense.setDescription(faker.lorem().sentence());

                lead.setCustomer(customer);
                lead.setManager(admin);
                lead.setName(faker.company().name() + " Opportunity");
                lead.setEmployee(admin);
                lead.setStatus(statuses[ThreadLocalRandom.current().nextInt(0, statuses.length)]);
                lead.setCreatedAt(LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(0, 60)));
                lead.setExpense(expense);

                leads.add(lead);
            }
        }
        return leads;
    }

    /**
     * Génère et sauvegarde des données aléatoires pour le CRM
     *
     * @param customerCount     Nombre de clients à générer
     * @param budgetPerCustomer Nombre de budgets par client
     * @param ticketPerCustomer Nombre de tickets par client
     * @param leadPerCustomer   Nombre de leads par client
     * @return Map contenant les statistiques des données générées
     * @throws SQLDataException si une erreur survient pendant la sauvegarde
     */
    @Transactional(rollbackFor = SQLDataException.class)
    public Map<String, Integer> generateAndSaveRandomData(int customerCount, int budgetPerCustomer,
            int ticketPerCustomer, int leadPerCustomer) throws SQLDataException {
        try {
            // 1. Générer et sauvegarder les clients
            List<CustomerLoginInfo> customerLoginInfos = generateRandomCustomers(customerCount);
            List<Customer> customers = new ArrayList<>();

            for (CustomerLoginInfo customerLoginInfo : customerLoginInfos) {
                entityManager.persist(customerLoginInfo.getCustomer());
                entityManager.persist(customerLoginInfo);
                customers.add(customerLoginInfo.getCustomer());
            }

            // 2. Générer et sauvegarder les budgets
            List<Budget> budgets = generateRandomBudgets(customers, budgetPerCustomer);
            for (Budget budget : budgets) {
                entityManager.persist(budget);
            }

            // 3. Générer et sauvegarder les tickets
            List<Ticket> tickets = generateRandomTickets(customers, ticketPerCustomer);
            for (Ticket ticket : tickets) {
                entityManager.persist(ticket.getExpense());
                entityManager.persist(ticket);
            }

            // 4. Générer et sauvegarder les leads
            List<Lead> leads = generateRandomLeads(customers, leadPerCustomer);
            for (Lead lead : leads) {
                entityManager.persist(lead.getExpense());
                entityManager.persist(lead);
            }

            // 5. Retourner des statistiques sur les données générées
            Map<String, Integer> stats = new HashMap<>();
            stats.put("customers", customers.size());
            stats.put("budgets", budgets.size());
            stats.put("tickets", tickets.size());
            stats.put("leads", leads.size());

            return stats;

        } catch (Exception e) {
            throw new SQLDataException("Erreur lors de la génération de données aléatoires: " + e.getMessage());
        }
    }
}
