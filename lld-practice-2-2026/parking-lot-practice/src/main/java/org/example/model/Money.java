package org.example.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Money implements Cloneable {

    private BigDecimal value;
    private Currency currency;

    @Override
    public Money clone() {
        return Money.builder()
                .value(value)
                .currency(currency)
                .build();
    }

    public enum Currency {
        IND
    }



}
