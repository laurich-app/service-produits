package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.rabbits.ProduitCommandeDTO;
import com.example.servicecatalogue.dtos.rabbits.ValiderCommandeDTO;
import com.example.servicecatalogue.exceptions.InvalideCommandeException;
import com.example.servicecatalogue.exceptions.QuantiteIndisponibleCommandeException;
import com.example.servicecatalogue.exceptions.StockNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceRabbitMQReceiver implements RabbitListenerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRabbitMQReceiver.class);

    private final ServiceProduit serviceProduit;

    public ServiceRabbitMQReceiver(@Autowired ServiceProduit serviceProduit) {
        this.serviceProduit = serviceProduit;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.commande.valider.commande}")
    public void consumeValiderCommande(ValiderCommandeDTO validerCommandeDTO) {
        logger.info("Commande validé : {}", validerCommandeDTO);
        try {
            serviceProduit.genereCommande(validerCommandeDTO);
        } catch (InvalideCommandeException | QuantiteIndisponibleCommandeException e) {
            logger.error(e.getMessage());
            logger.error(validerCommandeDTO.toString());
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.reapprovisionnement.stock.reappro}")
    public void consumeStockReappro(ProduitCommandeDTO produitCommandeDTO) {
        logger.info("Stock réapprovisionné : {}", produitCommandeDTO);
        try {
            serviceProduit.stockReappro(produitCommandeDTO);
        } catch (StockNotFoundException e) {
            logger.error(e.getMessage());
            logger.error(produitCommandeDTO.toString());
        }
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        // Nop
    }
}
