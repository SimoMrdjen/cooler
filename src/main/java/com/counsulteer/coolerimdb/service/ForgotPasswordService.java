package com.counsulteer.coolerimdb.service;

import com.counsulteer.coolerimdb.dto.ResetPasswordDto;

public interface ForgotPasswordService {
    void forgotPassword(String requestURI, String email);

    void resetPassword(ResetPasswordDto fpd);
}
