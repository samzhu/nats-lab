package com.example.internal.outboundservices;

import org.springframework.stereotype.Component;

import com.example.domain.events.AccountCreatedEvent;

@Component
public class AccountOutBound {
    
    public void publish(AccountCreatedEvent accountCreatedEvent){

    }
}
