package com.example.domain.valueobjects;

import lombok.Value;

public class BalanceVO {
    private Integer value;

    public BalanceVO(final Integer value) {
        this.value = value;
    }
}
