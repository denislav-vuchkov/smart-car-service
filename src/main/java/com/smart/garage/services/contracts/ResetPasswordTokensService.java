package com.smart.garage.services.contracts;

import com.smart.garage.models.ResetPasswordTokens;

public interface ResetPasswordTokensService {
    ResetPasswordTokens findByToken(String token);

    void create(ResetPasswordTokens token);

    void update(ResetPasswordTokens token);
}
