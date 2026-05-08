package org.example.user;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private List<Account> accountList;
    private List<Card> cardList;

    public User(String name) {
        this.name = name;
        this.accountList = new ArrayList<>();
        this.cardList = new ArrayList<>();
    }

    protected void addAccount(Account account) {
        accountList.add(account);
    }

    protected void addCard(Card card) {
        cardList.add(card);
    }






}
