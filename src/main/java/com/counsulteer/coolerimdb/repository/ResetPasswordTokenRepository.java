package com.counsulteer.coolerimdb.repository;

import com.counsulteer.coolerimdb.entity.ResetPasswordToken;
import com.counsulteer.coolerimdb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findByToken(String token);

    Optional<ResetPasswordToken> findByUser(User user);

    void deleteByToken(String token);
}
