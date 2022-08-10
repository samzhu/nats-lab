package com.example.interfaces.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.interfaces.dto.CreateAccountReqDTO;
import com.example.interfaces.dto.CreateAccountResDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "accounts")
public class AccountController {

    @PostMapping(path = "")
    public CreateAccountResDTO createAccount(@RequestBody CreateAccountReqDTO createAccountReqDTO) {
        log.info("收到 createAccountReqDTO={}", createAccountReqDTO);
        CreateAccountResDTO createAccountResDTO = new CreateAccountResDTO();

        return createAccountResDTO;

    }
}
