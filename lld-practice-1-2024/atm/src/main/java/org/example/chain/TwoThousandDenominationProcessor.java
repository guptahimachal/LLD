package org.example.chain;

import org.example.money.Denomination;

import java.util.HashMap;
import java.util.Map;

public class TwoThousandDenominationProcessor extends CashWithdrawProcessor {


    public TwoThousandDenominationProcessor(CashWithdrawProcessor cashWithdrawProcessor) {
        super(cashWithdrawProcessor);
    }

    @Override
    public Denomination getCurrentDenomination() {
        return Denomination.TWO_THOUSAND;
    }

    @Override
    public Boolean getDenomination(Integer amount, Map<Denomination, Integer> availableDenomination, Map<Denomination, Integer> withdrawDenomination) {

        int requiredNotes = Math.min(availableDenomination.get(getCurrentDenomination()), amount / getCurrentDenomination().getValue());

        if (requiredNotes > 0) {
            withdrawDenomination.put(getCurrentDenomination(), requiredNotes);
            amount -= requiredNotes * getCurrentDenomination().getValue();
        }

        if (amount == 0) {
            return true;
        }

        if (amount > 0 && nextProcessor != null) {
            return nextProcessor.getDenomination(amount, availableDenomination, withdrawDenomination);
        }

        return false;
    }

}
