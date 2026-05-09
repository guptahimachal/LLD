package org.example;

import org.example.atm.ATM;
import org.example.money.Denomination;
import org.example.user.Account;
import org.example.user.Card;
import org.example.user.User;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Map<Denomination, Integer> denominationIntegerMap = new HashMap<>();
        denominationIntegerMap.put(Denomination.FIVE_HUNDRED, 10);
        denominationIntegerMap.put(Denomination.HUNDRED, 100);

        ATM atm = new ATM(denominationIntegerMap);

        User user = new User("Ram");

        Account account = new Account(1, user);
        account.addBalance(5000);

        Card card = new Card(account, user, 2222);


        atm.getAtmState().insertCard(card);
        atm.getAtmState().validatePin();



    }
}