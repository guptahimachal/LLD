package org.example.entities.exception;

import org.example.entities.beverage.Beverage;
import org.example.entities.beverage.BeverageFactory;
import org.example.entities.beverage.BeverageType;

public class BeverageNotSupportedException extends RuntimeException {

    private BeverageType beverageType;

    public BeverageNotSupportedException(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

    @Override
    public String getMessage() {
        return String.format("%s beverage type not supported", beverageType);
    }

}
