package com.smart.garage.services;

import com.smart.garage.models.ResetPasswordTokens;
import com.smart.garage.repositories.contracts.ResetPasswordTokensRepository;
import com.smart.garage.services.contracts.ResetPasswordTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ResetPasswordTokensServiceImpl implements ResetPasswordTokensService {

    private final ResetPasswordTokensRepository resetPasswordTokensRepository;

    @Autowired
    public ResetPasswordTokensServiceImpl(ResetPasswordTokensRepository resetPasswordTokensRepository) {
        this.resetPasswordTokensRepository = resetPasswordTokensRepository;
    }

    @Override
    public ResetPasswordTokens findByToken(String token) {
        return resetPasswordTokensRepository.getByField("token", token);
    }

    @Override
    public void create(ResetPasswordTokens token) {
        resetPasswordTokensRepository.create(token);
    }

    @Override
    public void update(ResetPasswordTokens token) {
        resetPasswordTokensRepository.update(token);
    }


}
