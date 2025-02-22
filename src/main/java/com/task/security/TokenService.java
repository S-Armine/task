package com.task.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.jose4j.base64url.Base64;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

@ApplicationScoped
public class TokenService {

    public static int EXPIRATION_TIME_MILLIS = 15 * 60 * 1000;

    @ConfigProperty(name = "jwt.private.key")
    String privateKey;

    public String generateTokenString(String username) throws Exception {
        PrivateKey pk = generatePrivateKey();
        return generateTokenString(pk, username);
    }

    private String generateTokenString(PrivateKey privateKey, String username) throws Exception {
        JwtClaims claims = generateClaims(username);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(privateKey);
        jws.setHeader("type", "JWT");
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return jws.getCompactSerialization();
    }

    private JwtClaims generateClaims(String username) {
        long currentTimeMillis = System.currentTimeMillis();
        JwtClaims claims = new JwtClaims();
        claims.setSubject(username);
        claims.setIssuedAt(NumericDate.fromMilliseconds(currentTimeMillis));
        claims.setExpirationTime(NumericDate.fromMilliseconds(currentTimeMillis + EXPIRATION_TIME_MILLIS));
        return claims;
    }

    public PrivateKey generatePrivateKey() throws Exception {
        byte[] encodedBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }
}
