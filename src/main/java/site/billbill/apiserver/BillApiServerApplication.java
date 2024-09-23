package site.billbill.apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackages = "site.billbill.apiserver.model")
@ConfigurationPropertiesScan("site.billbill.apiserver.config")
public class BillApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillApiServerApplication.class, args);
    }

}
