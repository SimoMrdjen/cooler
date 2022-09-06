package com.counsulteer.coolerimdb.integrationtest;

import com.counsulteer.coolerimdb.CoolerimdbApplication;
import com.counsulteer.coolerimdb.dto.user.CreateUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CoolerimdbApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
        locations = {"classpath:application.properties", "classpath:application.yaml"}
)
public class RegistrationControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldCreateUser_whenCreateUser_thenStatus200() throws Exception{

        CreateUserDto createUserDTO =new CreateUserDto ("Ime","Prezime",
                "imeprezime@gmail.com","1Sifra!a");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(createUserDTO);
        MvcResult result = mvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                       .andReturn();
       String emailOfAddedUser = JsonPath.read(result.getResponse().getContentAsString(), "email");
       assertEquals(emailOfAddedUser, createUserDTO.getEmail());
    }

    @Test
    public void shouldReturnHttpMessageNotReadableExceptionWhenGivenEmptyBody() throws Exception {
        mvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
    }
}
