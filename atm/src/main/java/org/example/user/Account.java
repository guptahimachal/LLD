package org.example.user;

public class Account {

    private Integer number;
    private Integer balance;
    private User user;

    public Account(Integer number, User user) {
        this.number = number;
        this.user = user;
        this.balance = 0;
        this.user.addAccount(this);
    }

    public Integer getBalance() {
        return balance;
    }

    public void addBalance(Integer balance) {
        this.balance += balance;
    }

    public boolean deduceBalance(Integer balance) {
        if (balance > this.balance) {
            return false;
        }
        this.balance -= balance;
        return true;
    }



}
