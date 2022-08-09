package com.example.domain.aggregates;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.events.AccountCreatedEvent;
import com.example.domain.valueobjects.BalanceVO;
import com.example.internal.outboundservices.AccountOutBound;

@Document(collection = "accounts")
public class AccountAggregate {
    @Transient
    private AccountOutBound accountOutBound;

    @Id
    private String accountID;
    @Field("balance")
    private BalanceVO balanceVO;

    public void on(CreateAccountCommand createAccountCommand) {
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent();
        accountCreatedEvent.setAccountID(createAccountCommand.getAccountID());
        accountCreatedEvent.setBalanceVO(new BalanceVO(0));
        accountOutBound.publish(accountCreatedEvent);
    }

    public void on(AccountCreatedEvent accountCreatedEvent) {
        this.accountID = accountCreatedEvent.getAccountID();
        this.balanceVO = accountCreatedEvent.getBalanceVO();
    }
}
