package site.billbill.apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BillApiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillApiServerApplication.class, args);
	}

}
