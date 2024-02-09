package com.example.servicecatalogue.config;

import com.example.servicecatalogue.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;

@Service
public class CustomJwtDecoder {

    private final JwtProperties jwtProperties;

    public CustomJwtDecoder(@Autowired JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.jwtProperties.getKey()).build();
    }
}
