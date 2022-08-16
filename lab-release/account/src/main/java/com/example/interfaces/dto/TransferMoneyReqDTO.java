package com.example.interfaces.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferMoneyReqDTO {
    String fromAccountID;
    String toAccountID;
    Integer amount;
}
