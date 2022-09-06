package com.counsulteer.coolerimdb.integrationtest;

import com.counsulteer.coolerimdb.CoolerimdbApplication;
import com.counsulteer.coolerimdb.dto.user.UpdateUserDto;
import com.counsulteer.coolerimdb.entity.Role;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CoolerimdbApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
        locations = {"classpath:application.properties", "classpath:application.yaml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void init(){
        User user1 = new User(1L, "test1", "test1", "test1@test.com", Role.USER.toString());
        repository.save(user1);
        User user2 = new User( 2L, "test2", "test2", "test2@test.com", Role.USER.toString());
        repository.save(user2);
    }


    @Test
    public void shouldUpdateUser_thenStatus200() throws Exception {

       ObjectMapper om = new ObjectMapper();

        mvc.perform(MockMvcRequestBuilders
                        .put("/users/{id}", 1)
                        .content(om.writeValueAsString(new UpdateUserDto("firstName2", "lastName2", "email55@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("firstName2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("lastName2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email55@mail.com"));
    }


    @Test
    public void shouldGetUsers_whenGetUsers_thenStatus200() throws Exception {

        MvcResult result = mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andReturn();
       int sizeOfArray = new JSONArray(result.getResponse().getContentAsString()).length();
       assertEquals(2,sizeOfArray);

    }

    @Test
    public void shouldGetUserById_whenGet_thenStatus200() throws Exception {

        MvcResult result = mvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andReturn();
        String email = JsonPath.read(result.getResponse().getContentAsString(), "email");
        assertEquals("test2@test.com", email);
    }

    @Test
    public void shouldDeleteUserById_whenDelete_thenStatus200() throws Exception {

        mvc.perform( MockMvcRequestBuilders.delete("/users/{id}", 1) )
                        .andExpect(status().isOk());
    }



}

