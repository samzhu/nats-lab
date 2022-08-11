package com.example.interfaces.rest;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.commands.CreateAccountCommand;
import com.example.interfaces.dto.CreateAccountReqDTO;
import com.example.interfaces.dto.CreateAccountResDTO;
import com.example.interfaces.transform.AccountAssembler;
import com.example.internal.commandgateways.AccountCommandGateway;

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
    AccountAssembler accountAssembler;

    @PostMapping(path = "")
    public CreateAccountResDTO createAccount(@RequestBody CreateAccountReqDTO createAccountReqDTO) throws IOException {
        log.info("收到 createAccountReqDTO={}", createAccountReqDTO);
        CreateAccountResDTO createAccountResDTO = new CreateAccountResDTO();
        CreateAccountCommand createAccountCommand = accountAssembler.toCreateCartCommand(createAccountReqDTO);
        accountCommandGateway.createAccount(createAccountCommand);
        return createAccountResDTO;

    }
}
