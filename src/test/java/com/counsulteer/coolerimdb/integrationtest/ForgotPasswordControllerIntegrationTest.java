package com.counsulteer.coolerimdb.integrationtest;

import com.counsulteer.coolerimdb.dto.ResetPasswordDto;
import com.counsulteer.coolerimdb.entity.ResetPasswordToken;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.repository.ResetPasswordTokenRepository;
import com.counsulteer.coolerimdb.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(
        locations = {"classpath:application.properties", "classpath:application.yaml"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ForgotPasswordControllerIntegrationTest {
    @Value("${mail.username}")
    private String login;
    @Value("${mail.password}")
    private String password;
    private GreenMail greenMail;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Autowired
    private MockMvc mockMvc;
    private final User user = new User(1L, "Đorđe", "Čvarkov", "cvarkovibaba@pejicevisalasi.rs", "cvaresala");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String token;
    private String newUserPassword;
    private ResetPasswordToken resetPasswordToken;
    private ResetPasswordDto resetPasswordDto;

    @BeforeEach
    public void before() {
        userRepository.save(user);
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.withConfiguration(GreenMailConfiguration.aConfig().withUser(login, password));
        greenMail.start();
        token = UUID.randomUUID().toString();
        newUserPassword = UUID.randomUUID().toString();

    }

    @AfterEach
    public void after() {
        greenMail.stop();
    }

    @Test
    void shouldGetStatusOkWhenForgotPasswordCalled() throws Exception {
        mockMvc.perform(post("/users/forgotPassword/").param("email", user.getEmail()))
                .andDo(print())
                .andExpect(status().isOk());
        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(user.getEmail());

    }

    @Test
    void shouldThrowBadRequestWhenForgotUserEmailNotFound() throws Exception {
        mockMvc.perform(post("/users/forgotPassword/").param("email", "somemail@mail.com"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email not found!"));

    }

    @Test
    void shouldReturnStatusOKWhenResetPasswordCalled() throws Exception {
        resetPasswordToken = new ResetPasswordToken(token, LocalDateTime.MAX, user);
        resetPasswordToken.setId(1L);
        resetPasswordTokenRepository.save(resetPasswordToken);
        resetPasswordDto = new ResetPasswordDto(token, newUserPassword);
        mockMvc.perform(
                        post("/users/resetPassword/")
                                .content(objectMapper.writeValueAsString(resetPasswordDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());


    }

    @Test
    void shouldThrowBadRequestWhenResetPasswordWrongTokenGiven() throws Exception {
        resetPasswordToken = new ResetPasswordToken(token, LocalDateTime.MAX, user);
        resetPasswordToken.setId(1L);
        resetPasswordTokenRepository.save(resetPasswordToken);
        resetPasswordDto = new ResetPasswordDto(UUID.randomUUID().toString(), newUserPassword);
        mockMvc.perform(
                        post("/users/resetPassword/")
                                .content(objectMapper.writeValueAsString(resetPasswordDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wrong token!"));


    }

    @Test
    void shouldThrowBadRequestWhenForgotPasswordTokenHasExpired() throws Exception {
        resetPasswordToken = new ResetPasswordToken(token, LocalDateTime.now().minusDays(5L), user);
        resetPasswordToken.setId(1L);
        resetPasswordTokenRepository.save(resetPasswordToken);
        resetPasswordDto = new ResetPasswordDto(token, newUserPassword);
        mockMvc.perform(
                        post("/users/resetPassword/")
                                .content(objectMapper.writeValueAsString(resetPasswordDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token has expired!"));


    }
}