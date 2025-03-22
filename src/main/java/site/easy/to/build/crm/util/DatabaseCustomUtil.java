package site.easy.to.build.crm.util;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DatabaseCustomUtil {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Faker faker = new Faker();

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
        Set<String> tables = new HashSet<>(selectedTables);

        List<Integer> userIds = jdbcTemplate.queryForList("SELECT id FROM users", Integer.class);
        List<Integer> customerIds = new ArrayList<>();

        // Générer dans l'ordre en respectant les dépendances
        if (tables.contains("customer_login_info")) {
            //generateCustomerLoginInfos(count);
        }

        if (tables.contains("customer") || hasDependentTables(tables)) {
            //customerIds = generateCustomers(count, userIds);
        }

        if (tables.contains("trigger_lead")) {
            //generateLeads(count, customerIds, userIds);
        }

        List<Integer> leadIds = jdbcTemplate.queryForList("SELECT lead_id FROM trigger_lead", Integer.class);

        if (tables.contains("trigger_contract")) {
            //generateContracts(count, customerIds, userIds, leadIds);
        }

        if (tables.contains("trigger_ticket")) {
            //generateTickets(count, customerIds, userIds);
        }

        List<Integer> templateIds = jdbcTemplate.queryForList("SELECT template_id FROM email_template", Integer.class);
        List<Integer> customerLoginInfoIds = jdbcTemplate.queryForList("SELECT id FROM customer_login_info",
                Integer.class);

        if (tables.contains("contract_settings")) {
            //generateContractSettings(count, userIds, templateIds, customerLoginInfoIds);
        }

        if (tables.contains("ticket_settings")) {
            //generateTicketSettings(count, userIds, templateIds, customerLoginInfoIds);
        }

        if (tables.contains("lead_settings")) {
            //generateLeadSettings(count, userIds, templateIds, customerLoginInfoIds);
        }
    }


    private boolean hasDependentTables(Set<String> tables) {
        return tables.contains("trigger_contract") ||
                tables.contains("trigger_ticket") ||
                tables.contains("trigger_lead");
    }
}
