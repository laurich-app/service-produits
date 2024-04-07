package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.rabbits.GenererCommandeDTO;
import com.example.servicecatalogue.dtos.rabbits.ProduitCatalogueDTO;
import com.example.servicecatalogue.dtos.rabbits.SupprimerStockDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServiceRabbitMQSender {
    @Value("${spring.rabbitmq.exchange.catalogue.stock.supprimer}")
    private String exchangeCatalogueStockSupprimer;

    @Value("${spring.rabbitmq.routingkey.catalogue.stock.supprimer}")
    private String routingkeyCatalogueStockSupprimer;

    @Value("${spring.rabbitmq.exchange.catalogue.generer.commande}")
    private String exchangeCatalogueGenererCommande;

    @Value("${spring.rabbitmq.routingkey.catalogue.generer.commande}")
    private String routingkeyCatalogueGenererCommande;

    @Value("${spring.rabbitmq.exchange.catalogue.stock.manquant}")
    private String exchangeCatalogueStockManquant;

    @Value("${spring.rabbitmq.routingkey.catalogue.stock.manquant}")
    private String routingkeyCatalogueStockManquant;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ServiceRabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void supprimerStock(SupprimerStockDTO stockDTO){
        rabbitTemplate.convertAndSend(exchangeCatalogueStockSupprimer,routingkeyCatalogueStockSupprimer, stockDTO);
    }

    public void genererCommande(GenererCommandeDTO genererCommandeDTO) {
        rabbitTemplate.convertAndSend(exchangeCatalogueGenererCommande, routingkeyCatalogueGenererCommande, genererCommandeDTO);
    }

    public void stockManquant(ProduitCatalogueDTO p) {
        rabbitTemplate.convertAndSend(exchangeCatalogueStockManquant, routingkeyCatalogueStockManquant, p);
    }
}
