package com.example.domain.aggregates;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.commands.DepositMoneyCommand;
import com.example.domain.commands.WithdrawMoneyCommand;
import com.example.domain.events.AccountCreatedEvent;
import com.example.domain.events.MoneyDepositedEvent;
import com.example.domain.events.MoneyWithdrewEvent;
import com.example.internal.outboundservices.EventOutBound;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AccountAggregate {
    // @Transient
    @JsonIgnore
    private EventOutBound eventOutBound;
    private String accountID;
    private Integer balance;

    public AccountAggregate() {
    }

    public AccountAggregate(EventOutBound eventOutBound) {
        this.eventOutBound = eventOutBound;
    }

    public void on(CreateAccountCommand createAccountCommand) throws IOException {
        AccountCreatedEvent event = new AccountCreatedEvent();
        event.setEventID(NanoIdUtils.randomNanoId());
        event.setSubject("account.created");
        event.setAccountID(createAccountCommand.getAccountID());
        event.setBalance(0);
        event.setTime(LocalDateTime.now());
        eventOutBound.publish(event);
    }

    public void on(AccountCreatedEvent accountCreatedEvent) throws IOException {
        this.accountID = accountCreatedEvent.getAccountID();
        this.balance = accountCreatedEvent.getBalance();
    }

    public void on(DepositMoneyCommand command) throws IOException {
        MoneyDepositedEvent event = new MoneyDepositedEvent();
        event.setEventID(NanoIdUtils.randomNanoId());
        event.setSubject("account.deposited");
        event.setAccountID(command.getAccountID());
        event.setAmountOfDeposited(command.getAmount());
        event.setBalance(this.balance + command.getAmount());
        event.setTime(LocalDateTime.now());
        if (StringUtils.hasText(command.getTransactionID())) {
            event.setTransactionID(command.getTransactionID());
        }
        eventOutBound.publish(event);
    }

    public void on(MoneyDepositedEvent event) throws IOException {
        this.balance += event.getAmountOfDeposited();
    }

    public void on(WithdrawMoneyCommand command) throws IOException {
        MoneyWithdrewEvent event = new MoneyWithdrewEvent();
        event.setEventID(NanoIdUtils.randomNanoId());
        event.setSubject("account.withdraw");
        event.setAccountID(command.getAccountID());
        event.setAmountOfWithdrew(command.getAmount());
        event.setBalance(this.balance - command.getAmount());
        event.setTime(LocalDateTime.now());
        if (StringUtils.hasText(command.getTransactionID())) {
            event.setTransactionID(command.getTransactionID());
        }
        eventOutBound.publish(event);
    }

    public void on(MoneyWithdrewEvent event) throws IOException {
        this.balance -= event.getAmountOfWithdrew();
    }
}
