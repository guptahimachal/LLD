package org.example.chain;

import org.example.money.Denomination;

import java.util.Map;

public abstract class CashWithdrawProcessor {

    protected CashWithdrawProcessor nextProcessor;

    public CashWithdrawProcessor(CashWithdrawProcessor cashWithdrawProcessor) {
        this.nextProcessor = cashWithdrawProcessor;
    }

    public abstract Denomination getCurrentDenomination();

    public abstract Boolean getDenomination(Integer amount, Map<Denomination, Integer> availableDenomination, Map<Denomination, Integer> withdrawDenomination);




}
