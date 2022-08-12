package com.example.interfaces.transform;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.commands.DepositMoneyCommand;
import com.example.interfaces.dto.CreateAccountReqDTO;
import com.example.interfaces.dto.DepositMoneyReqDTO;

@Component
public class AccountAssembler {

    public CreateAccountCommand toCreateCartCommand(CreateAccountReqDTO createAccountReqDTO) {
        return new CreateAccountCommand(
                RandomStringUtils.randomNumeric(6), 0);
    }

    public DepositMoneyCommand toDepositMoneyCommand(DepositMoneyReqDTO depositMoneyReqDTO) {
        return new DepositMoneyCommand(
                depositMoneyReqDTO.getAccountID(), depositMoneyReqDTO.getAmount());
    }
}
