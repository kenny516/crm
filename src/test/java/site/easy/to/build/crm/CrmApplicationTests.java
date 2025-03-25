package site.easy.to.build.crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.easy.to.build.crm.util.CsvUtil;
import site.easy.to.build.crm.util.DatabaseCustomUtil;

import java.io.IOException;
import java.sql.SQLDataException;

@SpringBootTest
class CrmApplicationTests {

	@Autowired
	CsvUtil csvUtil;
	@Autowired
	DatabaseCustomUtil databaseCustomUtil;

	@Test
	void contextLoads() throws IOException, SQLDataException {

		databaseCustomUtil.generateAndSaveRandomData(10,10,10,10);



	}

}
