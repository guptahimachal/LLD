package org.example.atm;

import org.example.money.Denomination;
import org.example.state.ATMState;
import org.example.state.IdleState;

import java.util.HashMap;
import java.util.Map;

public class ATM {

    private Integer balance;
    private Map<Denomination, Integer> availableDenominations;
    private ATMState atmState;

    public ATM(Map<Denomination, Integer> availableDenominations) {
        availableDenominations = new HashMap<>();
        for(Map.Entry<Denomination, Integer> entry : availableDenominations.entrySet()) {
            balance += entry.getKey().getValue() * entry.getValue();
        }
        this.availableDenominations = availableDenominations;
        this.atmState = new IdleState(this);
    }

    public void setState(ATMState atmState) {
        this.atmState = atmState;
    }

    public Integer getBalance() {
        return balance;
    }

    public Map<Denomination, Integer> getAvailableDenominations() {
        return availableDenominations;
    }

    public ATMState getAtmState() {
        return atmState;
    }
}
