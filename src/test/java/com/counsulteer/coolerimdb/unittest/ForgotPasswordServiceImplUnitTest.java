package com.counsulteer.coolerimdb.unittest;

import com.counsulteer.coolerimdb.dto.ResetPasswordDto;
import com.counsulteer.coolerimdb.entity.ResetPasswordToken;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.repository.ResetPasswordTokenRepository;
import com.counsulteer.coolerimdb.repository.UserRepository;
import com.counsulteer.coolerimdb.service.MailSenderService;
import com.counsulteer.coolerimdb.service.impl.ForgotPasswordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceImplUnitTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private MailSenderService mailSenderService;
    @Mock
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ForgotPasswordServiceImpl forgotPasswordService;
    private User user;
    private String requestURI;
    private String newPassword;
    private ResetPasswordDto resetPasswordDto;
    private ResetPasswordToken resetPasswordToken;

    @BeforeEach
    public void beforeEach() {
        String token = UUID.randomUUID().toString();
        newPassword = "12345";
        user = new User(1L, "Nikola", "Protić", "nikola.protic@gmail.com", "123");
        resetPasswordDto = new ResetPasswordDto(token, newPassword);
        resetPasswordToken = new ResetPasswordToken(token, LocalDateTime.now().plusMinutes(5L), user);
        requestURI = "abc";

    }

    @Test
    void ShouldSendExpectedMailWhenForgotPasswordCalled() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        forgotPasswordService.forgotPassword(requestURI, user.getEmail());

        ArgumentCaptor<ResetPasswordToken> resetPasswordTokenArgumentCaptor = ArgumentCaptor.forClass(ResetPasswordToken.class);
        verify(resetPasswordTokenRepository).save(resetPasswordTokenArgumentCaptor.capture());
        ArgumentCaptor<String> emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailSenderService).sendEmail(emailArgumentCaptor.capture(), subjectArgumentCaptor.capture(), contentArgumentCaptor.capture());
        String capturedToken = resetPasswordTokenArgumentCaptor.getValue().getToken();
        String capturedEmail = emailArgumentCaptor.getValue();
        String capturedSubject = subjectArgumentCaptor.getValue();
        String capturedContent = contentArgumentCaptor.getValue();
        assertThat(capturedEmail).isEqualTo(user.getEmail());
        assertThat(capturedSubject).isEqualTo(user.getFirstName() + " " + user.getLastName() + " password reset");
        assertThat(capturedContent).isEqualTo(requestURI + "/new-password?token=" + capturedToken);


    }

    @Test
    void shouldThrowBadRequestWhenForgotPassowrdEmailIsWrong() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class).isThrownBy(
                () -> forgotPasswordService.forgotPassword(requestURI, user.getEmail())
        ).withMessage("Email not found!");
    }

    @Test
    void shouldUpdatePasswordWhenResetPasswordIsCalled() {
        resetPasswordToken.setId(1L);
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetPasswordToken));

        forgotPasswordService.resetPassword(resetPasswordDto);

        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Long> capturedId = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).save(capturedUser.capture());
        verify(resetPasswordTokenRepository).deleteById(capturedId.capture());
        user.setPassword(newPassword);
        assertThat(capturedUser.getValue()).isEqualTo(user);
        assertThat(capturedId.getValue()).isEqualTo(resetPasswordToken.getId());
    }

    @Test
    void shouldThrowBadRequestWhenResetPasswordTokenIsWrong() {
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class).isThrownBy(
                () -> forgotPasswordService.resetPassword(resetPasswordDto)
        ).withMessage("Wrong token!");
    }

    @Test
    void shouldThrowBadRequestWhenResetPasswordTokenExpired() {
        resetPasswordToken.setExpirationTime(LocalDateTime.now().minusDays(1L));
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetPasswordToken));

        assertThatExceptionOfType(BadRequestException.class).isThrownBy(
                () -> forgotPasswordService.resetPassword(resetPasswordDto)
        ).withMessage("Token has expired!");

    }
}