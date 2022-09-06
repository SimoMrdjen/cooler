package com.counsulteer.coolerimdb.unittest.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@SpringBootTest(classes = UserDetailsTestConfig.class)
@AutoConfigureMockMvc
public class SecurityLoginTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void shouldReturnUnauthenticatedTrueWhenGivenIncorrectDetails() throws Exception {
        String json = ("{\"email\": \"invalid\", \"password\": \"invalid\"}");

        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(unauthenticated());
    }

    @Test
    @WithUserDetails("user@gmail.com")
    public void shouldReturnStatusOkWhenAuthenticatedUserRequestsAllUsers() throws Exception {
        mvc.perform(get("/users").accept(MediaType.ALL)).andExpect(status().isOk());
    }

    @Test
    public void shouldAssertEqualsWithEmailStringWhenGetSubjectFromJwt() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        User user = new User(
                "user@gmail.com",
                "1234",
                List.of(new SimpleGrantedAuthority("USER")));

        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 60)) // 1 hour
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);

        assertEquals("user@gmail.com", JWT.decode(token).getSubject());
    }

    @Test
    public void shouldReturnRuntimeExceptionWhenGivenInvalidToken() {
        Exception exception = assertThrows(NestedServletException.class,()->
                mvc.perform(get("/token/refresh")
                        .header("Authorization", "invalid"))
                        .andExpect(status().isOk()));

        assertTrue(exception.getMessage().contains("Refresh token is missing"));
    }
}
