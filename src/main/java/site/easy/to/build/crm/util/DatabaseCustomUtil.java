package site.easy.to.build.crm.util;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerLoginInfo;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Service
public class DatabaseCustomUtil {

    private JdbcTemplate jdbcTemplate;
    private final Faker faker = new Faker();
    private final PasswordEncoder passwordEncoder;
    // service
    private UserService userService;
    private CustomerService customerService;

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


    @Transactional
    public void generateDataTable(List<String> selectedTables, int count) {

    }


    private List<Customer> generateCustomers(int count) {
        List<User> adminOrEmp = userService.findAll();
        List<Customer> customers = new ArrayList<>();
        for (User user : adminOrEmp) {
            for (int i = 0; i < count; i++) {
                String email = faker.name().username() + "@gmail.com";
                CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
                customerLoginInfo.setEmail(email);
                customerLoginInfo.setPasswordSet(true);
                String hashPassword = passwordEncoder.encode("2004");
                customerLoginInfo.setPassword(hashPassword);
                String token = EmailTokenUtils.generateToken();
                customerLoginInfo.setToken(token);
                // save le customerlogin
                Customer customer = new Customer();
                customer.setName(faker.name().username());
                customer.setPosition(faker.address().fullAddress());
                customer.setEmail(email);
                customer.setCustomerLoginInfo(customerLoginInfo);
                // save the customer
                customers.add(customer);
            }
        }
        return customers;
    }

//    public List<Ticket> generateTickets(int count) {
//        List<Customer> customers = customerService.findAll();
//        List<User> adminOrEmp = userService.findAll();
//        List<Ticket> tickets = new ArrayList<>();
//        for (User user : adminOrEmp) {
//            for (int i = 0; i < count; i++) {
//
//            }
//        }
//    }

}
