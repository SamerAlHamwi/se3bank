package com.bank.se3bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI se3BankOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:9090");
        server.setDescription("خادم التطوير");

        Contact contact = new Contact();
        contact.setEmail("bank@se3.edu");
        contact.setName("فريق SE3 Bank");
        contact.setUrl("https://se3bank.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("SE3 Bank API")
                .version("1.0")
                .contact(contact)
                .description("واجهة برمجة تطبيقات نظام البنك المتقدم")
                .termsOfService("https://se3bank.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}