package com.example.servicecatalogue;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ServiceCatalogueApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCatalogueApplication.class, args);
    }
}
