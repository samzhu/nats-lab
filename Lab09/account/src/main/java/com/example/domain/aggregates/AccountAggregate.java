package com.example.domain.aggregates;

import java.io.IOException;
import java.time.LocalDateTime;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.commands.DepositMoneyCommand;
import com.example.domain.events.AccountCreatedEvent;
import com.example.domain.events.MoneyDepositedEvent;
import com.example.internal.outboundservices.EventOutBound;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AccountAggregate {
    // @Transient
    @JsonIgnore
    private EventOutBound eventOutBound;

    // @Id
    private String accountID;
    // @Field("balance")
    private Integer balance;

    public AccountAggregate() {
    }

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

    public void on(DepositMoneyCommand depositMoneyCommand) throws IOException {
        MoneyDepositedEvent event = new MoneyDepositedEvent();
        event.setEventID(NanoIdUtils.randomNanoId());
        event.setAccountID(depositMoneyCommand.getAccountID());
        event.setAmountOfDeposited(0);
        event.setTime(LocalDateTime.now());
        // eventOutBound.publish(accountCreatedEvent);
    }




    
}
