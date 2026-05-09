package org.example.entities.beverage;

import org.example.entities.inventory.IngredientType;

import java.util.Map;

public class Americano extends Beverage {

    private static final Receipe RECEIPE = new Receipe(Map.of(
            IngredientType.COFFEE_POWDER, 10d,
            IngredientType.HOT_WATER, 100d));

    @Override
    public BeverageType getBeverageType() {
        return BeverageType.AMERICANO;
    }

    @Override
    public Receipe getReceipe() {
        return RECEIPE;
    }

}
