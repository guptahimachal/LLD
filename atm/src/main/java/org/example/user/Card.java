package org.example.user;

public class Card {

    private Account account;
    private User user;
    private Integer pin;

    public Card(Account account, User user, Integer pin) {
        this.account = account;
        this.user = user;
        this.pin = pin;
        this.user.addCard(this);
    }

    public boolean validatePin(Integer pin) {
        return pin.equals(this.pin);
    }

    public Account getAccount() {
        return account;
    }
}
