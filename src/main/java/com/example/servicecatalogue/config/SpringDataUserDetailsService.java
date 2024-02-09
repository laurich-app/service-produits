package com.example.servicecatalogue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpringDataUserDetailsService implements UserDetailsService {
    @Autowired
    JwtDecoder jwtDecoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Jwt jwt = jwtDecoder.decode(username);
        List<String> roles = (List<String>) jwt.getClaims().get("roles");

        /**
         * Pour avoir tous les rôles correctement SetUp avec JWT.
         * Le password doit obligatoirement être renseigné, mais je n'en voit pas l'utilité. L'important, c'est le subject() pour le username.
         * Avantage : pas besoin de recharger depuis la base les roles, on récupère directement depuis les tokens.
         */
        User.UserBuilder u = User.builder().username(jwt.getSubject()).password(jwt.getSubject());

        for(String role : roles)
            u.roles(role);

        return u.build();
    }
}
