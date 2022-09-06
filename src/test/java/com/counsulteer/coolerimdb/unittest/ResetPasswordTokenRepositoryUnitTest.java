package com.counsulteer.coolerimdb.unittest;

import com.counsulteer.coolerimdb.entity.ResetPasswordToken;
import com.counsulteer.coolerimdb.entity.User;
import com.counsulteer.coolerimdb.repository.ResetPasswordTokenRepository;
import com.counsulteer.coolerimdb.repository.UserRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ResetPasswordTokenRepositoryUnitTest {
//    @Autowired
//    private ResetPasswordTokenRepository resetPasswordTokenRepository;
//    @Autowired
//    private UserRepository userRepository;
//    private final String token = UUID.randomUUID().toString();
//    private ResetPasswordToken resetPasswordToken;
//
//    @BeforeEach
//    public void beforeEach() {
//        User user =  new User(1L, "Đorđe", "Čvarkov", "cvarkovibaba@pejicevisalasi.rs", "cvaresala");
//        userRepository.save(user);
//        resetPasswordToken = resetPasswordTokenRepository.save(
//                new ResetPasswordToken(1L, token, LocalDateTime.now().plusMinutes(5L), user)
//        );
//    }
//
//    @Test
//    public void shouldDeleteResetPasswordTokenWhenTokenGiven() {
//        resetPasswordTokenRepository.deleteByToken(token);
//        assertThat(resetPasswordTokenRepository.findById(resetPasswordToken.getId()).isPresent()).isFalse();
//    }
//
//    @Test
//    public void shouldUpdateResetPasswordTokenWhenCalled() {
//        String newToken = UUID.randomUUID().toString();
//        resetPasswordToken.setToken(newToken);
//        resetPasswordTokenRepository.save(resetPasswordToken);
//
//        assertThat(resetPasswordTokenRepository.findById(resetPasswordToken.getId()).isPresent()).isTrue();
//        assertThat(resetPasswordTokenRepository.findById(resetPasswordToken.getId()).get().getToken()).isEqualTo(newToken);
//    }
//
//    @Test
//    public void shouldFindByTokenWhenCalled() {
//        Optional<ResetPasswordToken> resetPasswordTokenOptional = resetPasswordTokenRepository.findByToken(token);
//        assertThat(resetPasswordTokenOptional.isPresent()).isTrue();
//        assertThat(resetPasswordTokenOptional.get()).isEqualTo(resetPasswordToken);
//    }

}