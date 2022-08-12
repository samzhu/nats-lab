package com.example.internal.commandgateways;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.example.domain.aggregates.AccountAggregate;
import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.commands.DepositMoneyCommand;
import com.example.internal.outboundservices.EventOutBound;
import com.example.internal.querygateways.AccountQueryGateway;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AccountCommandGateway {

    EventOutBound eventOutBound;
    AccountQueryGateway accountQueryGateway;

    public void createAccount(CreateAccountCommand createAccountCommand) throws IOException {
        AccountAggregate accountAggregate = new AccountAggregate(eventOutBound);
        accountAggregate.on(createAccountCommand);
    }

    public void depositMoney(DepositMoneyCommand depositMoneyCommand) throws IOException {
        AccountAggregate accountAggregate = accountQueryGateway.findByAccountID(depositMoneyCommand.getAccountID());
        accountAggregate.on(depositMoneyCommand);
    }

}
