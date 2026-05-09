package org.example.entities.beverage;

import org.example.entities.exception.BeverageNotSupportedException;

public class BeverageFactory {

    public static Beverage getBeverage(BeverageType beverageType) {
        switch (beverageType) {
            case AMERICANO -> {
                return new Americano();
            }
            case ESPRESSO -> {
                return new Espresso();
            }
            case LATTE -> {
                return new Latte();
            }
            default -> throw new BeverageNotSupportedException(beverageType);
        }

    }





}
