package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.ProduitUpdateDTO;
import com.example.servicecatalogue.dtos.StockDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.out.StocksOutDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.enums.Sexe;
import com.example.servicecatalogue.enums.Taille;
import com.example.servicecatalogue.exceptions.StockExisteDejaException;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.modele.Stocks;
import com.example.servicecatalogue.services.ServiceProduit;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TestProduitsController extends TestConfigurationControlleurRest {

    @MockBean
    ServiceProduit serviceProduit;

    @MockBean
    Validator validator;


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


        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,Sexe.HOMME,Taille.S);
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
        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,Sexe.HOMME,Taille.S);
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
        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,Sexe.HOMME,Taille.S);
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
        ProduitDTO produitDTO = new ProduitDTO(new ArrayList<>(), "libelle","desc",1,"img",1,Sexe.HOMME,Taille.S);
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
     * Si non admin
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

    /**
     * Si admin ok
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateProduitOk(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Stocks s = new Stocks();
        List<Stocks> lStock = new ArrayList<>();
        lStock.add(s);

        Categorie spyCategorie = Mockito.spy(new Categorie());
        Mockito.when(spyCategorie.getIdCategorie()).thenReturn(1);

        Produit spyProduit = Mockito.spy(new Produit());
        Mockito.when(spyProduit.getStocks()).thenReturn(lStock);
        Mockito.when(spyProduit.getCategory()).thenReturn(spyCategorie);

        ProduitUpdateDTO produitDTO = new ProduitUpdateDTO("libelle","desc",1,"img",1, Sexe.HOMME, Taille.S);
        // Définie l'admin en admin
        this.defineAdminUser();
        doReturn(spyProduit).when(serviceProduit).updateProduit(1, produitDTO);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Si no admin Forbidden
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateProduitFobidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        ProduitUpdateDTO produitDTO = new ProduitUpdateDTO("libelle","desc",1,"img",1, Sexe.HOMME, Taille.S);


        // WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * Si non connecter UNAUTHORIZED
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateProduitUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        ProduitUpdateDTO produitDTO = new ProduitUpdateDTO("libelle","desc",1,"img",1, Sexe.HOMME, Taille.S);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/produits/1")
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /**
     * Si not found
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateProduitNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        ProduitUpdateDTO produitDTO = new ProduitUpdateDTO("libelle","desc",1,"img",1, Sexe.HOMME, Taille.S);
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(EntityNotFoundException.class).when(serviceProduit).updateProduit(1, produitDTO);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/produits/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(produitDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /*========== getAllProduits ==========*/

    /**
     * Si Ok
     * @param mvc
     * @throws Exception
     */
    @Test
    void testGetAllProduitsOk(@Autowired MockMvc mvc) throws Exception {
        // BEFORE
        Paginate<ProduitOutPaginateDTO> p = new Paginate<>(List.of(), new Pagination(0, 10, 0));

        doReturn(p).when(serviceProduit).getAllProduits(any());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Si Ok
     * @param mvc
     * @throws Exception
     */
    @Test
    void testGetAllProduitsViolation(@Autowired MockMvc mvc) throws Exception {
        // BEFORE
        Set<ConstraintViolation<PaginateRequestDTO>> mocked = mock(Set.class);
        doReturn(mocked).when(this.validator).validate(any(PaginateRequestDTO.class));

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    /*========== deleteStock ==========*/

    /**
     * Si admin ok
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteStockOk(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Couleurs couleurs = Couleurs.BLANC;
        // Définie l'admin en admin
        this.defineAdminUser();
        doNothing().when(serviceProduit).deleteStock(1, couleurs);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1/couleurs/BLANC")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(couleurs))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }


    /**
     * Si admin Not found
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteStockNOtFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Couleurs couleurs = Couleurs.BLANC;
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(EntityNotFoundException.class).when(serviceProduit).deleteStock(1, couleurs);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1/couleurs/BLANC")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(couleurs))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /**
     * Si admin Not admin Forbidden
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteStockForbidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Couleurs couleurs = Couleurs.BLANC;

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1/couleurs/BLANC")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(couleurs))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * Si admin Not connected unauthorized
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testDeleteStockUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Couleurs couleurs = Couleurs.BLANC;

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/produits/1/couleurs/BLANC")
                        .content(objectMapper.writeValueAsString(couleurs))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /*========== addStock ==========*/

    /**
     * Si admin ok
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testAddStockOk(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        StockDTO stockDTO = new StockDTO(Couleurs.BLANC);
        Stocks stock = Mockito.spy(Stocks.class);
        // Définie l'admin en admin
        this.defineAdminUser();
        doReturn(stock).when(serviceProduit).addStock(1, stockDTO.couleur());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits/1/couleurs")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(stockDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    /**
     * Si admin couleur not found
     *
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testAddStockNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        StockDTO stockDTO = new StockDTO(Couleurs.BLANC);
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(EntityNotFoundException.class).when(serviceProduit).addStock(1, stockDTO.couleur());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits/1/couleurs")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(stockDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /**
     * Si admin stock existe deja
     *
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testAddStockConflict(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        StockDTO stockDTO = new StockDTO(Couleurs.BLANC);
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(StockExisteDejaException.class).when(serviceProduit).addStock(1, stockDTO.couleur());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits/1/couleurs")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(stockDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    /**
     * Si not admin forbidden
     *
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testAddStockForbidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        StockDTO stockDTO = new StockDTO(Couleurs.BLANC);


        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits/1/couleurs")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(stockDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * Si not admin forbidden
     *
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testAddStockUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        StockDTO stockDTO = new StockDTO(Couleurs.BLANC);


        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/produits/1/couleurs")
                        .content(objectMapper.writeValueAsString(stockDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
}
