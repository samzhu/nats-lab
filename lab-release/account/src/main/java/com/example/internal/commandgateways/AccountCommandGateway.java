package com.example.internal.commandgateways;

import java.io.IOException;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.example.constant.SagaState;
import com.example.constant.TransactionState;
import com.example.domain.aggregates.AccountAggregate;
import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.commands.DepositMoneyCommand;
import com.example.domain.commands.TransferMoneyCommand;
import com.example.domain.commands.WithdrawMoneyCommand;
import com.example.domain.saga.TransferSaga;
import com.example.internal.outboundservices.EventOutBound;
import com.example.internal.querygateways.AccountQueryGateway;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    MongoTemplate mongoTemplate;
    ObjectMapper objectMapper;

    public void createAccount(CreateAccountCommand createAccountCommand) throws IOException {
        AccountAggregate accountAggregate = new AccountAggregate(eventOutBound);
        accountAggregate.on(createAccountCommand);
    }

    public void depositMoney(DepositMoneyCommand depositMoneyCommand) throws IOException {
        AccountAggregate accountAggregate = accountQueryGateway.findByAccountID(depositMoneyCommand.getAccountID());
        accountAggregate.on(depositMoneyCommand);
    }

    public void transferMoney(TransferMoneyCommand transferMoneyCommand) throws IOException {
        TransferSaga transferSaga = new TransferSaga(transferMoneyCommand.getTransactionID(),
                transferMoneyCommand.getFromAccountID(),
                transferMoneyCommand.getToAccountID(), transferMoneyCommand.getAmount(), TransactionState.BEGINNING,
                TransactionState.BEGINNING, SagaState.START);
        mongoTemplate.save(transferSaga, "transfer");

        AccountAggregate fromAccountAggregate = accountQueryGateway
                .findByAccountID(transferMoneyCommand.getFromAccountID());
        WithdrawMoneyCommand withdrawMoneyCommand = transferMoneyCommand.toWithdrawMoneyCommand();
        fromAccountAggregate.on(withdrawMoneyCommand);

        AccountAggregate toAccountAggregate = accountQueryGateway
                .findByAccountID(transferMoneyCommand.getToAccountID());
        DepositMoneyCommand depositMoneyCommand = transferMoneyCommand.toDepositMoneyCommand();
        toAccountAggregate.on(depositMoneyCommand);

    }

}
