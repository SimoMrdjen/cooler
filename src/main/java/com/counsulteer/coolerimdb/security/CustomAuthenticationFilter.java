package com.counsulteer.coolerimdb.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.counsulteer.coolerimdb.exception.UnauthorizedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String UNAUTHORIZED_EXCEPTION_MESSAGE = "Authentication has failed or was not provided!";

    private final AuthenticationManager authenticationManager;
    private final SecurityUtility securityUtility;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = null;
        String password = null;

        try {
            Map<String, String> requestBody = getMapFromRequestBody(request);
            email = requestBody.get("email");
            password = requestBody.get("password");
        } catch (IOException e) {
            throw new UnauthorizedException(UNAUTHORIZED_EXCEPTION_MESSAGE);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        List<String> userRoles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String accessToken = securityUtility.createAccessToken(user.getUsername(), request, userRoles);
        String refreshToken = createRefreshToken(user, request);

        securityUtility.sendAccessAndRefreshTokens(accessToken, refreshToken, response);
    }

    public String createRefreshToken(User user, HttpServletRequest request) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 3600000)) // 1 day
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    public Map<String, String> getMapFromRequestBody(HttpServletRequest request) throws IOException {
        String lines = request.getReader()
                .lines()
                .collect(Collectors.joining());

        return objectMapper
                .readValue(
                        lines,
                        new TypeReference<>() {}
                );
    }
}