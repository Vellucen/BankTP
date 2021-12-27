package fr.ul.miage.bank;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankServiceApplication.class, args);
    }

    @Bean
    public OpenAPI bankAPI(){
        return new OpenAPI().info(new Info()
                .title("Bank API")
                .version("1.0")
                .description("Description de l'API Bank"));
    }
}
