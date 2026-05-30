package org.example.entities.beverage;

import org.example.entities.inventory.IngredientType;

import java.util.Map;

public class Latte extends Beverage {

    private static final Receipe RECEIPE = new Receipe(Map.of(
            IngredientType.COFFEE_POWDER, 10d,
            IngredientType.HOT_WATER, 40d,
            IngredientType.MILK, 40d));

    @Override
    public BeverageType getBeverageType() {
        return BeverageType.LATTE;
    }

    @Override
    public Receipe getReceipe() {
        return RECEIPE;
    }

}
