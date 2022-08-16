package com.example.interfaces.rest;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.aggregates.AccountAggregate;
import com.example.domain.commands.CreateAccountCommand;
import com.example.domain.commands.DepositMoneyCommand;
import com.example.domain.commands.TransferMoneyCommand;
import com.example.interfaces.dto.AccountViewDTO;
import com.example.interfaces.dto.CreateAccountReqDTO;
import com.example.interfaces.dto.DepositMoneyReqDTO;
import com.example.interfaces.dto.TransferMoneyReqDTO;
import com.example.interfaces.transform.AccountAssembler;
import com.example.internal.commandgateways.AccountCommandGateway;
import com.example.internal.querygateways.AccountQueryGateway;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "accounts")
public class AccountController {

    AccountCommandGateway accountCommandGateway;
    AccountQueryGateway accountQueryGateway;
    AccountAssembler accountAssembler;

    @PostMapping(path = "")
    public ResponseEntity<Void> createAccount(@RequestBody CreateAccountReqDTO createAccountReqDTO) throws IOException {
        log.info("收到 createAccountReqDTO={}", createAccountReqDTO);
        // CreateAccountResDTO createAccountResDTO = new CreateAccountResDTO();
        CreateAccountCommand createAccountCommand = accountAssembler.toCreateCartCommand(createAccountReqDTO);
        accountCommandGateway.createAccount(createAccountCommand);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping(path = "/{accountID}")
    public ResponseEntity<AccountViewDTO> queryAccount(@PathVariable(name = "accountID") String accountID) throws IOException {
        AccountAggregate accountAggregate = accountQueryGateway.findByAccountID(accountID);
        AccountViewDTO accountViewDTO = accountAssembler.toAccountView(accountAggregate);
        return ResponseEntity.status(HttpStatus.OK).body(accountViewDTO);
    }

    @PostMapping(path = "/deposit")
    public ResponseEntity<Void> depositMoney(@RequestBody DepositMoneyReqDTO depositMoneyReqDTO) throws IOException {
        DepositMoneyCommand depositMoneyCommand = accountAssembler.toDepositMoneyCommand(depositMoneyReqDTO);
        accountCommandGateway.depositMoney(depositMoneyCommand);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(path = "/transfer")
    public void transferMoney(@RequestBody TransferMoneyReqDTO transferMoneyReqDTO) throws IOException {
        TransferMoneyCommand transferMoneyCommand = accountAssembler.toTransferMoneyCommand(transferMoneyReqDTO);
        accountCommandGateway.transferMoney(transferMoneyCommand);

    }

}
