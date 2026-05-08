package org.example.state;

import org.example.atm.ATM;
import org.example.money.MaxFirstDenominationStrategy;
import org.example.user.Card;

import java.util.Scanner;

public class AuthenticatedState extends ATMState {

    private Card card;

    public AuthenticatedState(ATM atm, Card card) {
        super(atm);
        this.card = card;
    }

    @Override
    public StateName getStateName() {
        return StateName.AUTHENTICATED;
    }

    @Override
    public void chooseTask() {
        System.out.println("Choose Options ");
        System.out.println("1. Cash withdraw");
        System.out.println("2. Change Pin");
        System.out.println("3. Show balance");

        Scanner scanner = new Scanner(System.in);
        Integer choosenOption = scanner.nextInt();

        switch (choosenOption) {
            case 1 -> {
                atm.setState(new WithdrawState(atm, card, new MaxFirstDenominationStrategy()));
                return;
            }
//            case 2 -> {
//
//            }

            default -> {
                System.out.println("Undefined Option choosen");
                this.cancel();
            }
        }


    }

    @Override
    public void cancel() {
        System.out.println("Please take out ");
        atm.setState(new IdleState(atm));
    }




}
