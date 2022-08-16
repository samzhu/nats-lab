package com.example.domain.saga;

import org.springframework.data.annotation.Id;

import com.example.constant.SagaState;
import com.example.constant.TransactionState;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferSaga {
    @Id
    String transactionID;
    String sourceBankAccountId;
    String destinationBankAccountId;
    Integer amount;

    TransactionState withdrawMoneyState;
    TransactionState depositMoneyState;
    SagaState transferState;
}
