package fr.ul.miage.shop;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShopServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopServiceApplication.class, args);
    }

    @Bean
    public OpenAPI shopAPI(){
        return new OpenAPI().info(new Info()
                .title("Shop API")
                .version("1.0")
                .description("API Shop : "));
    }
}
