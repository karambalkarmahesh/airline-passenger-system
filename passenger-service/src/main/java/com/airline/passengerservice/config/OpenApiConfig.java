package com.airline.passengerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI passengerServiceOpenAPI() {

        Contact contact = new Contact()
                .name("Airline Development Team")
                .email("support@airline.com");

        Info apiInfo = new Info()
                .title("Passenger Service API")
                .description(
                        "REST APIs for managing passengers in the Airline Passenger Service System"
                )
                .version("1.0.0")
                .contact(contact);

        return new OpenAPI()
                .info(apiInfo);
    }
}