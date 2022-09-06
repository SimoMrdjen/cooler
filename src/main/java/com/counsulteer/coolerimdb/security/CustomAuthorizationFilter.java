package com.counsulteer.coolerimdb.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final SecurityUtility securityUtility;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (servletPathIsCorrect(request)) {
            filterChain.doFilter(request, response);
        } else {
            String header = request.getHeader(AUTHORIZATION);

            if (securityUtility.authorizationHeaderNotNullAndStartsCorrectly(header)) {
                try {
                    doFilter(header, request, response, filterChain);
                } catch (Exception e) {
                    log.error("Error logging in: {}", e.getMessage());
                    securityUtility.displayErrorMessageAsJson(response, e);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    public Boolean servletPathIsCorrect(HttpServletRequest request) {
        return request.getServletPath().equals("/login") || request.getServletPath().equals("/token/refresh");
    }

    public void doFilter(String authorizationHeader, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = authorizationHeader.substring("Bearer ".length());
        String email = securityUtility.getEmailFromJwtCode(token);
        Collection<SimpleGrantedAuthority> authorities = turnUserRolesIntoAuthorities(token);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    public Collection<SimpleGrantedAuthority> turnUserRolesIntoAuthorities(String token) {
        DecodedJWT decodedJWT = securityUtility.getDecodedJwt(token);

        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return authorities;
    }

}
