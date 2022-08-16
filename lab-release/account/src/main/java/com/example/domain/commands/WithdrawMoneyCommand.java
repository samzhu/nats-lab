package com.example.domain.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WithdrawMoneyCommand {
    String transactionID;
    String accountID;
    Integer amount;
}
