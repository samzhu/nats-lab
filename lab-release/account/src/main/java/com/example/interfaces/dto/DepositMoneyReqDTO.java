package com.example.interfaces.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepositMoneyReqDTO {
    String accountID;
    Integer amount;
}
