package org.example.state;

import org.example.atm.ATM;
import org.example.money.Denomination;
import org.example.money.DenominationStrategy;
import org.example.user.Card;

import java.util.Map;
import java.util.Scanner;

public class WithdrawState extends ATMState {

    private Card card;
    private DenominationStrategy denominationStrategy;

    public WithdrawState(ATM atm, Card card, DenominationStrategy denominationStrategy) {
        super(atm);
        this.card = card;
        this.denominationStrategy = denominationStrategy;
    }


    @Override
    public StateName getStateName() {
        return StateName.WITHDRAW;
    }


    @Override
    public void withdraw() {

        System.out.println("Enter amount : ");
        Scanner scanner = new Scanner(System.in);
        Integer requestedAmount = scanner.nextInt();
        Integer availableAccBalance = card.getAccount().getBalance();
        Integer availableATMBalance = atm.getBalance();

        if (requestedAmount > availableAccBalance) {
            System.out.println("Insufficient fund in Account");
            cancel();
        }

        if (requestedAmount > availableATMBalance) {
            System.out.println("Insufficient fund in ATM");
            cancel();
        }

        Map<Denomination, Integer> outputDenominations = denominationStrategy.getPossibleDenomination(requestedAmount, atm.getAvailableDenominations());



    }




}
