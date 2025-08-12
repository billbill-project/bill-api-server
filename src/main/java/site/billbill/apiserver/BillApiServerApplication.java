package site.billbill.apiserver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@EnableRabbit
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackages = "site.billbill.apiserver.model")
@ConfigurationPropertiesScan("site.billbill.apiserver.config")
@OpenAPIDefinition(
        servers = {
                @Server(url = "http://3.37.239.99/", description = "dev server"),
                @Server(url = "http://localhost:8080", description = "local server"),
        }
)
public class BillApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillApiServerApplication.class, args);
    }

}
