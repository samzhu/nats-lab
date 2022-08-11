package com.example.domain.valueobjects;

import lombok.Data;
import lombok.Value;

@Data
public class BalanceVO {
    private Integer value;

    public BalanceVO(final Integer value) {
        this.value = value;
    }
}
