package org.example.state;

import org.example.atm.ATM;
import org.example.user.Card;

import java.util.Scanner;

public class HasCardState extends ATMState {

    private Card card;

    public HasCardState(ATM atm, Card card) {
        super(atm);
        this.card = card;
    }

    @Override
    public StateName getStateName() {
        return StateName.HAS_CARD;
    }

    @Override
    public void validatePin() {
        Scanner scanner = new Scanner(System.in);

        for (int attempt = 0 ; attempt < 3 ; attempt++) {
            System.out.println("Attempt " + attempt+1 + " Enter pin : ");
            Integer pin = scanner.nextInt();

            if (card.validatePin(pin)) {
                atm.setState(new AuthenticatedState(atm, card));
                return;
            }

        }
        System.out.println("All attempt exhausted");
        this.cancel();
    }

    @Override
    public void cancel() {
        System.out.println("Please take out ");
        atm.setState(new IdleState(atm));
    }



}
