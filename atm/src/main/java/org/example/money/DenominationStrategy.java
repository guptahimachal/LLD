package org.example.money;

import java.util.Map;

public interface DenominationStrategy {

    Map<Denomination, Integer> getPossibleDenomination(Integer amount, Map<Denomination, Integer> availableDenomination);

}
