package com.example.servicecatalogue.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQReapprovisionnementStockReappro {
    @Value("${spring.rabbitmq.queue.reapprovisionnement.stock.reappro}")
    private String queue;
    @Value("${spring.rabbitmq.exchange.reapprovisionnement.stock.reappro}")
    private String exchange;
    @Value("${spring.rabbitmq.routingkey.reapprovisionnement.stock.reappro}")
    private String routingKey;

    @Bean
    Queue queueReapproStockReappro() {
        return new Queue(queue, true);
    }

    @Bean
    Exchange myExchangeReapproStockReappro() {
        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    @Bean
    Binding bindingReapproStockReappro() {
        return BindingBuilder
                .bind(queueReapproStockReappro())
                .to(myExchangeReapproStockReappro())
                .with(routingKey)
                .noargs();
    }
}
