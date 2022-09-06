package com.counsulteer.coolerimdb.entity;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reset_password_token")
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;

    public ResetPasswordToken(String token, LocalDateTime expirationTime, User user) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetPasswordToken that = (ResetPasswordToken) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token) && Objects.equals(expirationTime, that.expirationTime) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, expirationTime, user);
    }

    @Override
    public String toString() {
        return "ResetPasswordToken{" +
                ", token='" + token + '\'' +
                ", expirationTime=" + expirationTime +
                ", user=" + user +
                '}';
    }
}
