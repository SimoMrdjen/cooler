package com.counsulteer.coolerimdb.service.impl;

import com.counsulteer.coolerimdb.dto.user.UserDto;
import com.counsulteer.coolerimdb.security.SecurityUtility;
import com.counsulteer.coolerimdb.service.TokenService;
import com.counsulteer.coolerimdb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final UserService userService;
    private final SecurityUtility securityUtility = new SecurityUtility();

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader(AUTHORIZATION);

        if (securityUtility.authorizationHeaderNotNullAndStartsCorrectly(header)) {
            try {
                String refreshToken = header.substring("Bearer ".length());
                String email = getUserFromJwtCode(refreshToken).getEmail();
                List<String> userRoles = List.of(getUserFromJwtCode(refreshToken).getRole().toString());

                String accessToken = securityUtility.createAccessToken(email, request, userRoles);

                securityUtility.sendAccessAndRefreshTokens(accessToken, refreshToken, response);

            } catch (Exception e) {
                securityUtility.displayErrorMessageAsJson(response, e);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    public UserDto getUserFromJwtCode(String refreshToken) {
        String email = securityUtility.getEmailFromJwtCode(refreshToken);
        return userService.getUserByEmail(email);
    }
}