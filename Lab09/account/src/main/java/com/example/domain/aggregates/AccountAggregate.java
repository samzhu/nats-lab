package com.example.domain.aggregates;

import java.io.IOException;
import java.time.LocalDateTime;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.events.AccountCreatedEvent;
import com.example.domain.valueobjects.BalanceVO;
import com.example.internal.outboundservices.EventOutBound;


public class AccountAggregate {
    // @Transient
    private EventOutBound eventOutBound;

    // @Id
    private String accountID;
    // @Field("balance")
    private Integer balance;

    public AccountAggregate(EventOutBound eventOutBound) {
        this.eventOutBound = eventOutBound;
    }

    public void on(CreateAccountCommand createAccountCommand) throws IOException {
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent();
        accountCreatedEvent.setEventID(NanoIdUtils.randomNanoId());
        accountCreatedEvent.setAccountID(createAccountCommand.getAccountID());
        accountCreatedEvent.setBalance(0);
        accountCreatedEvent.setTime(LocalDateTime.now());
        eventOutBound.publish(accountCreatedEvent);
    }

    public void on(AccountCreatedEvent accountCreatedEvent) throws IOException {
        this.accountID = accountCreatedEvent.getAccountID();
        this.balance = accountCreatedEvent.getBalance();
    }
}
