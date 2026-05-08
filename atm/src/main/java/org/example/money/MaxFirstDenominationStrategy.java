package org.example.money;

import java.util.Map;

public class MaxFirstDenominationStrategy implements DenominationStrategy {

    @Override
    public Map<Denomination, Integer> getPossibleDenomination(Integer amount, Map<Denomination, Integer> availableDenomination) {

        return Map.of(Denomination.FIVE_HUNDRED, 1);
    }

}
