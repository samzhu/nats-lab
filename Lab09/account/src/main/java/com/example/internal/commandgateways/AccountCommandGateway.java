package com.example.internal.commandgateways;

import org.springframework.stereotype.Component;

import com.example.domain.aggregates.AccountAggregate;
import com.example.domain.commands.CreateAccountCommand;
import com.example.internal.outboundservices.AccountOutBound;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountCommandGateway {

    private final AccountOutBound accountOutBound;

    public void createAccount(CreateAccountCommand createAccountCommand){
        AccountAggregate accountAggregate = new AccountAggregate(accountOutBound);
        accountAggregate.on(createAccountCommand);
    }

    
}
