package com.smart.garage.repositories;

import com.smart.garage.models.ResetPasswordTokens;
import com.smart.garage.repositories.contracts.ResetPasswordTokensRepository;
import org.springframework.stereotype.Repository;


@Repository
public class ResetPasswordTokensRepositoryImpl extends AbstractCRUDRepository<ResetPasswordTokens> implements ResetPasswordTokensRepository {

    public ResetPasswordTokensRepositoryImpl() {
        super(ResetPasswordTokens.class);
    }

}
