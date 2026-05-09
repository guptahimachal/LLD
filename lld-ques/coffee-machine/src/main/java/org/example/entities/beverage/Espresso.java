package org.example.entities.beverage;

import org.example.entities.inventory.IngredientType;

import java.util.Map;

public class Espresso extends Beverage {

    private static final Receipe RECEIPE = new Receipe(Map.of(
            IngredientType.COFFEE_POWDER, 10d,
            IngredientType.HOT_WATER, 20d));

    @Override
    public BeverageType getBeverageType() {
        return BeverageType.ESPRESSO;
    }

    @Override
    public Receipe getReceipe() {
        return RECEIPE;
    }

}
