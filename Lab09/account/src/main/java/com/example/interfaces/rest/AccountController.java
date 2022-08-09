package com.example.interfaces.rest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.interfaces.dto.CreateAccountReqDTO;
import com.example.interfaces.dto.CreateAccountResDTO;

@RestController
public class AccountController {

    public CreateAccountResDTO createAccount(@RequestBody CreateAccountReqDTO createAccountReqDTO) {
        CreateAccountResDTO createAccountResDTO = new CreateAccountResDTO();

        return createAccountResDTO;

    }
}
