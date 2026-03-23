package com.pecenio.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "com.pecenio.backend",
                "com.pecenio.database"
        }
)
@EnableJpaRepositories(basePackages = "com.pecenio.database.repository")
@EntityScan(basePackages = "com.pecenio.database.entity")
public class SupportTicketingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupportTicketingApplication.class, args);
    }
}

