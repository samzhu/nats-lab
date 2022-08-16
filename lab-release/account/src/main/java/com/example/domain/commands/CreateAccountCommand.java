package com.example.domain.commands;

import com.example.domain.valueobjects.BalanceVO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 建立帳號命令
 */
@Data
@AllArgsConstructor
public class CreateAccountCommand {
    private String accountID;
    private Integer balance;
}
