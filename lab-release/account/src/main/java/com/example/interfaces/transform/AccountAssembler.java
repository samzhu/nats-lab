package com.example.interfaces.transform;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.domain.aggregates.AccountAggregate;
import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.commands.DepositMoneyCommand;
import com.example.domain.commands.TransferMoneyCommand;
import com.example.interfaces.dto.AccountViewDTO;
import com.example.interfaces.dto.CreateAccountReqDTO;
import com.example.interfaces.dto.DepositMoneyReqDTO;
import com.example.interfaces.dto.TransferMoneyReqDTO;

@Component
public class AccountAssembler {

    public CreateAccountCommand toCreateCartCommand(CreateAccountReqDTO createAccountReqDTO) {
        return new CreateAccountCommand(
                RandomStringUtils.randomNumeric(6), 0);
    }

    public DepositMoneyCommand toDepositMoneyCommand(DepositMoneyReqDTO depositMoneyReqDTO) {
        return new DepositMoneyCommand(null,
                depositMoneyReqDTO.getAccountID(), depositMoneyReqDTO.getAmount());
    }

    public TransferMoneyCommand toTransferMoneyCommand(TransferMoneyReqDTO transferMoneyReqDTO) {
        return new TransferMoneyCommand(
                NanoIdUtils.randomNanoId(),
                transferMoneyReqDTO.getFromAccountID(), transferMoneyReqDTO.getToAccountID(),
                transferMoneyReqDTO.getAmount());
    }

    public AccountViewDTO toAccountView(AccountAggregate accountAggregate) {
        return new AccountViewDTO(
            accountAggregate.getAccountID(),
            accountAggregate.getBalance());
    }
}
