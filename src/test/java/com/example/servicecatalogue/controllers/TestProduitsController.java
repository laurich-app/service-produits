package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.services.ServiceProduit;
import org.springframework.boot.test.mock.mockito.MockBean;

public class TestProduitsController extends TestConfigurationControlleurRest {

    @MockBean
    ServiceProduit serviceProduit;
}
