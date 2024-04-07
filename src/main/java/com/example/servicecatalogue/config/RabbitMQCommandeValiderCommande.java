package com.example.servicecatalogue.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQCommandeValiderCommande {
    @Value("${spring.rabbitmq.queue.commande.valider.commande}")
    private String queue;
    @Value("${spring.rabbitmq.exchange.commande.valider.commande}")
    private String exchange;
    @Value("${spring.rabbitmq.routingkey.commande.valider.commande}")
    private String routingKey;

    @Bean
    Queue queue() {
        return new Queue(queue, true);
    }

    @Bean
    Exchange myExchange() {
        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(myExchange())
                .with(routingKey)
                .noargs();
    }
}
