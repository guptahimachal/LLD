package org.example.state;

import org.example.atm.ATM;
import org.example.user.Card;

public class IdleState extends ATMState {

    public IdleState(ATM atm) {
        super(atm);
    }

    @Override
    public StateName getStateName() {
        return StateName.IDLE;
    }

    public void insertCard(Card card) {
        System.out.println("Card is inserted");
        atm.setState(new HasCardState(atm, card));
    }




}
