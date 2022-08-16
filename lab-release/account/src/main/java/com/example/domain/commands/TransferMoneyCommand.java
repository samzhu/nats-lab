package com.example.domain.commands;

import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferMoneyCommand {
    String transactionID;
    String fromAccountID;
    String toAccountID;
    Integer amount;

    public WithdrawMoneyCommand toWithdrawMoneyCommand() {
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(transactionID, fromAccountID, amount);
        return withdrawMoneyCommand;
    }

    public DepositMoneyCommand toDepositMoneyCommand() {
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(transactionID, toAccountID, amount);
        return depositMoneyCommand;
    }
}
