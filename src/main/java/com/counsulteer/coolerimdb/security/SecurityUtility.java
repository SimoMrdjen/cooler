package com.counsulteer.coolerimdb.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class SecurityUtility {

    public void displayErrorMessageAsJson(HttpServletResponse response, Exception e) throws IOException {
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    public Boolean authorizationHeaderNotNullAndStartsCorrectly(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    public DecodedJWT getDecodedJwt(String token) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String getEmailFromJwtCode(String token) {
        DecodedJWT decodedJWT = getDecodedJwt(token);
        return decodedJWT.getSubject();
    }

    public void sendAccessAndRefreshTokens(String accessToken, String refreshToken, HttpServletResponse response) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    public String createAccessToken(String email, HttpServletRequest request, List<String> userRoles) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", userRoles)
                .sign(algorithm);
    }
}
