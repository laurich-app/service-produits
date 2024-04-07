package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.dtos.CategorieDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.exceptions.CategorieNotFoundException;
import com.example.servicecatalogue.services.ServiceCategorie;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class TestCategoriesController extends TestConfigurationControlleurRest {

    @MockBean
    private ServiceCategorie serviceCategorie;

    @MockBean
    private Validator validator;

    /*========== CreerCategories ==========*/

    /**
     * Si non connecté, Unauthorized
     * @param mvc
     * @throws Exception
     */
    @Test
    void testCreerCategoriesUnauthorized(@Autowired MockMvc mvc) throws Exception {
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
    void testCreerCategoriesNotAdminForbidden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
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
     * Si admin, Created
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

    /*========== GetAllCategories ==========*/

    /**
     * Si contrainte violation ok
     * @param mvc
     * @throws Exception
     */
    @Test
    void testGetAllCategoriesOk(@Autowired MockMvc mvc) throws Exception {
        // BEFORE
        Paginate<CategorieDTO> p = new Paginate<>(List.of(), new Pagination(0, 10, 0));

        doReturn(p).when(serviceCategorie).getAllCategories(any());

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Si violation : bad request
     * @param mvc
     * @throws Exception
     */
    @Test
    void testGetAllCategoriesViolation(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // BEFORE
        Set<ConstraintViolation<PaginateRequestDTO>> mocked = mock(Set.class);
        doReturn(mocked).when(this.validator).validate(any(PaginateRequestDTO.class));

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    /*========== GetCategoryDetails ==========*/

    /**
     * Categorie Not Found
     * @param mvc
     * @throws Exception
     */
    @Test
    void  testgetCategoryDetailsNotFound(@Autowired MockMvc mvc) throws Exception {
        //BEFORE
        doThrow(CategorieNotFoundException.class).when(serviceCategorie).getCategoryById(2);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/categories/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /**
     *  Categorie found : ok
     * @param mvc
     * @throws Exception
     */
    @Test
    void  testgetCategoryDetailsOk(@Autowired MockMvc mvc) throws Exception {
        //BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 2);
        doReturn(categorieDTO).when(serviceCategorie).getCategoryById(1);

        // WHERE
        MockHttpServletResponse response = mvc.perform(
                get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /*========== UpdateCategory ==========*/

    /**
     * Si admin et Categorie found : Ok
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateCategoryOk(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        //BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 2);
        // Définie l'admin en admin
        this.defineAdminUser();
        doReturn(categorieDTO).when(serviceCategorie).updateCategory(eq(1), any(CategorieDTO.class));

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/categories/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(categorieDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    /**
     * Si non connecte : Unauthorized
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateCategoryUnauthorized(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        //BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 2);

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/categories/1")
                        .content(objectMapper.writeValueAsString(categorieDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /**
     * Si non admin, Forbidden
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateCategoryNotAdminForbiden(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        //BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 2);

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/categories/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(categorieDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    /**
     * Si Categorie not found
     * @param mvc
     * @param objectMapper
     * @throws Exception
     */
    @Test
    void testUpdateCategoryCategoryNotFound(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) throws Exception {
        //BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 2);
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(CategorieNotFoundException.class).when(serviceCategorie).updateCategory(eq(2), any(CategorieDTO.class));

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                put("/categories/2")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .content(objectMapper.writeValueAsString(categorieDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /*========== deleteCategory ==========*/

    /**
     * Si admin et Categorie found : Not_Content
     * @param mvc
     * @throws Exception
     */
    @Test
    void testDeleteCategoryOk(@Autowired MockMvc mvc) throws Exception {
        //BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 2);
        // Définie l'admin en admin
        this.defineAdminUser();
        doNothing().when(serviceCategorie).deleteCategory(1);

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/categories/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    /**
     * Si admin et Categorie not found : Not found
     * @param mvc
     * @throws Exception
     */
    @Test
    void testDeleteCategoryNotFound(@Autowired MockMvc mvc) throws Exception {
        //BEFORE
        CategorieDTO categorieDTO = new CategorieDTO(1, "libelle", 2);
        // Définie l'admin en admin
        this.defineAdminUser();
        doThrow(CategorieNotFoundException.class).when(serviceCategorie).deleteCategory(2);

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/categories/2")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    /**
     * Si non connecte : Unauthorized
     * @param mvc
     * @throws Exception
     */
    @Test
    void testDeleteCategoryUnothorized(@Autowired MockMvc mvc) throws Exception {

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    /**
     * Si non admin : Forbidden
     * @param mvc
     * @throws Exception
     */
    @Test
    void testDeleteCategoryForbidden(@Autowired MockMvc mvc) throws Exception {

        //WHERE
        MockHttpServletResponse response = mvc.perform(
                delete("/categories/1")
                        .header("Authorization", "Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        //WHEN
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }


}
