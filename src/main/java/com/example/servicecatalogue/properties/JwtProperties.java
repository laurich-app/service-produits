package com.example.servicecatalogue.properties;

import com.example.servicecatalogue.dtos.ConsulDTO;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "jwt.public")
public class JwtProperties {

    private RSAPublicKey key;

    private final WebClient webClient;

    @Value("${CONSUL_HOST:localhost}")
    private String consulHost;

    @Value("${CONSUL_PORT:8500}")
    private String consulPort;

    @PostConstruct
    public void postConstruct() throws Exception {
        ConsulDTO[] results = this.webClient.get().uri("http://"+consulHost+":"+consulPort+"/v1/kv/config/application/publicKey").retrieve().bodyToMono(ConsulDTO[].class).block();
        if(results.length == 0)
            throw new Exception("Aucune clé trouvé");

        // Décodage Base64
        byte[] decodedKeyBytes = Base64.getDecoder().decode(results[0].Value());

        // Convertir les bytes en une chaîne de caractères
        String decodedKeyString = new String(decodedKeyBytes, Charset.defaultCharset());

        String publicKeyPEM = decodedKeyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "");

        // Convertir la chaîne de caractères en un tableau de bytes (byte[])
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        //         Créez une spécification de clé X.509 à partir des données décodées
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);

        //         Obtenez une instance de la fabrique de clés RSA
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        //         Générez la clé publique à partir de la spécification
        PublicKey publicKey = keyFactory.generatePublic(spec);

        // Assurez-vous que la clé est une instance de RSAPublicKey
        if (publicKey instanceof RSAPublicKey) {
            this.setKey((RSAPublicKey) publicKey);
        } else {
            throw new IllegalArgumentException("La clé fournie n'est pas une clé publique RSA valide.");
        }
    }

    public JwtProperties() {
        this.webClient = WebClient.builder().build();
    }

    // Ajoutez les getters et setters nécessaires

    public RSAPublicKey getKey() {
        return this.key;
    }

    public void setKey(RSAPublicKey key) {
        this.key = key;
    }
}

