package com.counsulteer.coolerimdb.unittest;

import com.counsulteer.coolerimdb.controller.ForgotPasswordController;
import com.counsulteer.coolerimdb.dto.ResetPasswordDto;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.service.ForgotPasswordService;
import com.counsulteer.coolerimdb.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ForgotPasswordController.class})
@AutoConfigureMockMvc(addFilters = false)
class ForgotPasswordControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ForgotPasswordService forgotPasswordService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResetPasswordDto resetPasswordDto = new ResetPasswordDto(
            UUID.randomUUID().toString(),
            "12345"
    );
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void shouldGetStatusOkWhenForgotPasswordCalled() throws Exception {
        doNothing().when(forgotPasswordService).forgotPassword(anyString(), anyString());

        mockMvc.perform(post("/users/forgotPassword/").param("email", "nikola.protic@consulteer.com"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowBadRequestWhenForgotUserEmailNotFound() throws Exception {
        doThrow(new BadRequestException("Email not found!")).when(forgotPasswordService).forgotPassword(anyString(), anyString());

        mockMvc.perform(post("/users/forgotPassword/").param("email", "nikola.protic@consulteer.com"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email not found!"));
    }

    @Test
    void shouldReturnStatusOKWhenResetPasswordCalled() throws Exception {
        mockMvc.perform(post("/users/resetPassword/")
                        .content(objectMapper.writeValueAsString(resetPasswordDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowBadRequestWhenResetPasswordWrongTokenGiven() throws Exception {
        doThrow(new BadRequestException("Wrong token!")).when(forgotPasswordService).resetPassword(any(ResetPasswordDto.class));

        mockMvc.perform(post("/users/resetPassword/")
                        .content(objectMapper.writeValueAsString(resetPasswordDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wrong token!"));
    }

    @Test
    void shouldThrowBadRequestWhenForgotPasswordTokenHasExpired() throws Exception {
        doThrow(new BadRequestException("Token has expired!")).when(forgotPasswordService).resetPassword(any(ResetPasswordDto.class));

        mockMvc.perform(post("/users/resetPassword/")
                        .content(objectMapper.writeValueAsString(resetPasswordDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token has expired!"));
    }
}