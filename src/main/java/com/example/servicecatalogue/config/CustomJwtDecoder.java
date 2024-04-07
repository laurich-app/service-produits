package com.example.servicecatalogue.config;

import com.example.servicecatalogue.properties.JwtProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@AllArgsConstructor
@Configuration
public class CustomJwtDecoder {

    @Bean
    JwtDecoder jwtDecoder(@Autowired JwtProperties jwtProperties) {
        return NimbusJwtDecoder.withPublicKey(jwtProperties.getKey()).build();
    }
}
