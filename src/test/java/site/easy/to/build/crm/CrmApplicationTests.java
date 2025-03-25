package site.easy.to.build.crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.easy.to.build.crm.DTO.BudgetDtoCsv;
import site.easy.to.build.crm.DTO.CustomerDtoCsv;
import site.easy.to.build.crm.DTO.TicketLeadDtoCsv;
import site.easy.to.build.crm.entity.File;
import site.easy.to.build.crm.util.CsvUtil;
import site.easy.to.build.crm.util.DatabaseCustomUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
class CrmApplicationTests {

	@Autowired
	CsvUtil csvUtil;

	@Test
	void contextLoads() throws IOException {
		///  import Customer
		InputStream customercStream = new FileInputStream("D:\\L3\\S6\\Ecaluation\\crm\\csv-file\\customer.csv");
		StringBuilder erroCustomer = new StringBuilder();
		List<CustomerDtoCsv> customerDtoCsvs = csvUtil.read(CustomerDtoCsv.class,customercStream,erroCustomer);

		for (CustomerDtoCsv customerDtoCsv : customerDtoCsvs) {
			System.out.println("Customer email " +customerDtoCsv.getEmail());
			System.out.println("Customer name " +customerDtoCsv.getName());
		}
		System.out.println("Customer error "+erroCustomer);

		///  import budget
		InputStream budgetStream = new FileInputStream("D:\\L3\\S6\\Ecaluation\\crm\\csv-file\\budget.csv");
		StringBuilder erroBudget = new StringBuilder();
		List<BudgetDtoCsv> budgetDtoCsvs = csvUtil.read(BudgetDtoCsv.class,budgetStream,erroBudget);
		for (BudgetDtoCsv budgetDtoCsv : budgetDtoCsvs) {
			System.out.println("Budget email " +budgetDtoCsv.getCustomerEmail());
			System.out.println("Budget " +budgetDtoCsv.getBudget());
		}
		System.out.println("Budget Error "+erroBudget);

		///  import Ticket lead
		InputStream ticketLeadStream = new FileInputStream("D:\\L3\\S6\\Ecaluation\\crm\\csv-file\\TicketLead.csv");
		StringBuilder errorTicketLead = new StringBuilder();
		List<TicketLeadDtoCsv> ticketLeadDtoCsvs = csvUtil.read(TicketLeadDtoCsv.class,ticketLeadStream,errorTicketLead);
		for (TicketLeadDtoCsv ticketLeadDtoCsv : ticketLeadDtoCsvs) {
			System.out.println("TicketLead email " +ticketLeadDtoCsv.getCustomerEmail());
			System.out.println("TicketLead name or Subject " +ticketLeadDtoCsv.getSubjectOrName());
			System.out.println("TicketLead type " +ticketLeadDtoCsv.getType());
			System.out.println("TicketLead status " +ticketLeadDtoCsv.getStatus());
			System.out.println("TicketLead expense "+ticketLeadDtoCsv.getExpense());
		}
		System.out.println("Ticket error "+ errorTicketLead);




	}

}
