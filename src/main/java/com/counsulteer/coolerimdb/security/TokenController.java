package com.counsulteer.coolerimdb.security;

import com.counsulteer.coolerimdb.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @Operation(summary = "Refresh access token", description = "Returns a new access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token has been refreshed.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Refresh token is missing.",
                    content = @Content)})
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        tokenService.refreshToken(request, response);
    }

}
