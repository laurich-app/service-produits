package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.modele.Stocks;
import com.example.servicecatalogue.services.ServiceProduit;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TestProduitsController extends TestConfigurationControlleurRest {

    @MockBean
    ServiceProduit serviceProduit;




    /*========== saveProduit ==========*/

    /**
     * Ok
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testSaveProduitOk(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Stocks s = new Stocks();
        List<Stocks> lStock = new ArrayList<>();
        lStock.add(s);

        Categorie spyCategorie = Mockito.spy(new Categorie());
        Mockito.when(spyCategorie.getIdCategorie()).thenReturn(1);

        Produit spyProduit = Mockito.spy(new Produit());
        Mockito.when(spyProduit.getStocks()).thenReturn(lStock);
        Mockito.when(spyProduit.getCategory()).thenReturn(spyCategorie);


        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,"S","123");
        // Définie l'admin en admin
        this.defineAdminUser();
        doReturn(spyProduit).when(serviceProduit).saveProduit(produitDTO);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    /**
     * Si non connecté
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testSaveProduitUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

    }

    /**
     * Si non admin
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testSaveProduitNotAdminFobidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,"S","123");
        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * Si bad request
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testSaveProduitIllegalArgument(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,"S","123");
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(IllegalArgumentException.class).when(serviceProduit).saveProduit(produitDTO);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    /**
     * Si Entity not found
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testSaveProduitNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,"S","123");
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(EntityNotFoundException.class).when(serviceProduit).saveProduit(produitDTO);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /*========== getProduitById ===========*/

    /**
     * Ok
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testGetProduitByIdOk(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Stocks s = new Stocks();
        List<Stocks> lStock = new ArrayList<>();
        lStock.add(s);

        Categorie spyCategorie = Mockito.spy(new Categorie());
        Mockito.when(spyCategorie.getIdCategorie()).thenReturn(1);

        Produit spyProduit = Mockito.spy(new Produit());
        Mockito.when(spyProduit.getStocks()).thenReturn(lStock);
        Mockito.when(spyProduit.getCategory()).thenReturn(spyCategorie);

        // Définie l'admin en admin
        this.defineAdminUser();
        doReturn(spyProduit).when(serviceProduit).getProduitById(1);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Entity not found
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testGetProduitByIdNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(EntityNotFoundException.class).when(serviceProduit).getProduitById(1);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /*========== deleteProduit ==========*/

    /**
     * Ok
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteProduitOk(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // Définie l'admin en admin
        this.defineAdminUser();
        doNothing().when(serviceProduit).deleteProduit(1);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    /**
     * Entity not found
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteProduitNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(EntityNotFoundException.class).when(serviceProduit).deleteProduit(1);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /**
     * Si non connecté
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteProduitUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /**
     * ESi non admin
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteProduitForbidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        doThrow(EntityNotFoundException.class).when(serviceProduit).deleteProduit(1);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /*========== updateProduit ==========*/



}
