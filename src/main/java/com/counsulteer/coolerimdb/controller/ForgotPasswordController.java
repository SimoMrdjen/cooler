package com.counsulteer.coolerimdb.controller;

import com.counsulteer.coolerimdb.dto.ResetPasswordDto;
import com.counsulteer.coolerimdb.service.ForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/users")
@AllArgsConstructor
@Slf4j
public class ForgotPasswordController {
    private final ForgotPasswordService forgotPasswordService;

    @Operation(summary = "Sends an email to user", description = "Sends a forgot password email to the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email has been sent.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Email is not found.",
                    content = @Content)})
    @PostMapping(value = "/forgotPassword")
    public void forgotPassword(HttpServletRequest request, @RequestParam("email") String email) {
        log.info("Reset password token requested for user with email: " + email);
        String url = request.getRequestURL().toString().replace(request.getServletPath(), "");
        forgotPasswordService.forgotPassword(url, email);
    }

    @Operation(summary = "Reset password", description = "Resets the password of the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password has been reset.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "The token is wrong.",
                    content = @Content)})
    @PostMapping(value = "/resetPassword")
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        log.info("Request to use following reset password token: " + resetPasswordDto.toString());
        forgotPasswordService.resetPassword(resetPasswordDto);
    }
}
