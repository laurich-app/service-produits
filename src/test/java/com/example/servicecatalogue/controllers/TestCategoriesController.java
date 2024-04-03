package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.services.ServiceCategorie;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class TestCategoriesController extends TestConfigurationControlleurRest {

    @MockBean
    private ServiceCategorie serviceCategorie;

    /**
     * Si non connecté, Unauthorized
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testCreerCategoriesUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /**
     * Si non admin, Forbidden
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testCreerCategoriesNotAdminUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 10);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/categories")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(categorieDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * Si non admin, Forbidden
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testCreerCategoriesAdmin(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 10);
        // Définie l'admin en admin
        this.defineAdminUser();
        doReturn(categorieDTO).when(serviceCategorie).createCategory(categorieDTO);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                post("/categories")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(categorieDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }
}
