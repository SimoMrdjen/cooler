package com.counsulteer.coolerimdb.service.impl;

import com.counsulteer.coolerimdb.dto.ResetPasswordDto;
import com.counsulteer.coolerimdb.entity.ResetPasswordToken;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.exception.BadRequestException;
import com.counsulteer.coolerimdb.repository.ResetPasswordTokenRepository;
import com.counsulteer.coolerimdb.repository.UserRepository;
import com.counsulteer.coolerimdb.service.ForgotPasswordService;
import com.counsulteer.coolerimdb.service.MailSenderService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;
    private final MailSenderService mailSenderService;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void forgotPassword(String requestURI, String email) {
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            String token = generateToken(user);
            mailSenderService.sendEmail(user.getEmail(),
                    user.getFirstName() + " " + user.getLastName() + " password reset",
                    requestURI + "/new-password?token=" + token);
        }, () -> {
            throw new BadRequestException("Email not found!");
        });

    }

    @Override
    public void resetPassword(ResetPasswordDto fpd) {

        resetPasswordTokenRepository.findByToken(fpd.getToken()).ifPresentOrElse(
                (resetPasswordToken) -> {
                    if (resetPasswordToken.getExpirationTime().isBefore(LocalDateTime.now()))
                        throw new BadRequestException("Token has expired!");
                    else {
                        User user = resetPasswordToken.getUser();
                        user.setPassword(passwordEncoder.encode(fpd.getNewPassword()));
                        userRepository.save(user);
                        resetPasswordTokenRepository.deleteById(resetPasswordToken.getId());
                    }
                },
                () -> {
                    throw new BadRequestException("Wrong token!");
                }
        );
    }

    private String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        resetPasswordTokenRepository.findByUser(user).ifPresentOrElse(t -> {
            t.setExpirationTime(expirationTime);
            t.setToken(token);
            resetPasswordTokenRepository.save(t);
        }, () -> {
            ResetPasswordToken rpt = new ResetPasswordToken(token, expirationTime, user);
            resetPasswordTokenRepository.save(rpt);
        });

        return token;
    }
}
