package com.counsulteer.coolerimdb.unittest;

import com.counsulteer.coolerimdb.controller.UserController;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.mapper.UserMapper;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, useDefaultFilters = true)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    public BCryptPasswordEncoder passwordEncoder;

    private User user;
    private UserMapper userMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        user = new User(1L,
                "ime",
                "prezime",
                "imeprezime@consulteer.com",
                "1234");

        userMapper = new UserMapper();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldReturnJsonArrayWhenGetUsersCalled() throws Exception {
        List<User> allUsers = List.of(user);

        given(userService.getUsers())
                .willReturn(allUsers
                        .stream()
                        .map(userMapper::mapEntityToDto)
                        .collect(Collectors.toList()));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is(user.getFirstName())));
    }

    @Test
    public void shouldReturnStatusOkWhenUpdatingUser() throws Exception {
        List<User> allUsers = List.of(user);

        given(userService.getUsers())
                .willReturn(allUsers
                        .stream()
                        .map(userMapper::mapEntityToDto)
                        .collect(Collectors.toList()));

        User userSecond = new User(
                1L,
                "luka",
                "luka",
                "luka@consulteer.com",
                "12345678"
        );

        String json = objectMapper.writeValueAsString(userSecond);

        mvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldReturnStatusOkWhenDeletingUser() throws Exception {
        List<User> allUsers = List.of(user);

        given(userService.getUsers())
                .willReturn(allUsers
                        .stream()
                        .map(userMapper::mapEntityToDto)
                        .collect(Collectors.toList()));

        mvc.perform(delete("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}


