package com.example.domain.commands;

import com.example.domain.valueobjects.BalanceVO;

import lombok.Data;

/**
 * 建立帳號命令
 */
@Data
public class CreateAccountCommand {
    private String accountID;
    private BalanceVO balanceVO;
}
