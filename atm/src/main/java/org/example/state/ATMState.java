package org.example.state;

import org.example.atm.ATM;
import org.example.user.Card;

public abstract class ATMState {

    protected ATM atm;

    public ATMState(ATM atm) {
        this.atm = atm;
    }

    public abstract StateName getStateName();

    public void insertCard(Card card) {
        throw new RuntimeException(String.format("Insert card operation not allowed in %s state", getStateName()));
    }

    public void validatePin() {
        throw new RuntimeException(String.format("Validate Pin operation not allowed in %s state", getStateName()));
    }

    public void chooseTask() {
        throw new RuntimeException(String.format("Choose Task operation not allowed in %s state", getStateName()));
    }

    public void withdraw() {
        throw new RuntimeException(String.format("Withdraw operation not allowed in %s state", getStateName()));
    }

    public void showBalance() {
        throw new RuntimeException(String.format("Show Balance operation not allowed in %s state", getStateName()));
    }

    public void changePin() {
        throw new RuntimeException(String.format("ChangePin operation not allowed in %s state", getStateName()));
    }

    public void cancel() {
        throw new RuntimeException(String.format("Cancel operation not allowed in %s state", getStateName()));
    }




}
