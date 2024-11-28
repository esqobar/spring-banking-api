package com.collins.bank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Banking API",
                version = "1.0",
                description = "A simple banking API built with Spring Boot",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Collins Omondi",
                        email = "collecollins@gmail.com",
                        url = "https://github.com/esqobar/spring-bank-api"
                ),
                license = @License(
                        name = "Just Java",
                        url = "https://github.com/esqobar/spring-bank-api"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Just Java Bank App Documentation",
                url = "https://github.com/esqobar/spring-bank-api"
        )
)
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

}
