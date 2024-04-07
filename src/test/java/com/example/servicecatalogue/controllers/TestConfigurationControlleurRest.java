package com.example.servicecatalogue.controllers;

import com.example.servicecatalogue.repositories.CategorieRepository;
import com.example.servicecatalogue.repositories.ProduitRepository;
import com.example.servicecatalogue.repositories.StocksRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing
@SpringBootTest(properties = { "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration" })
@AutoConfigureMockMvc
public abstract class TestConfigurationControlleurRest {

    /**
     * Pour le startup.
     */
    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private CategorieRepository categorieRepository;

    @MockBean
    private ProduitRepository produitRepository;

    @MockBean
    private StocksRepository stocksRepository;

    private String accessToken;

    private Map<String, Object> claims;

    @BeforeEach
    public void init() {
        accessToken = "accessToken";
        Jwt jwt = mock(Jwt.class);
        doReturn(jwt).when(jwtDecoder).decode(accessToken);
        claims = new HashMap<>();
        doReturn(claims).when(jwt).getClaims();
        claims.put("roles", List.of("USER"));
        doReturn("1").when(jwt).getSubject();
    }

    public String getAccessToken() {
        return accessToken;
    }

    protected void defineAdminUser() {
        claims.put("roles", List.of("USER", "GESTIONNAIRE"));
    }

    /**
     * Permet de récupérer les base URI, pour les TESTS
     * @return
     */
    protected String getBaseUri(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }
}
