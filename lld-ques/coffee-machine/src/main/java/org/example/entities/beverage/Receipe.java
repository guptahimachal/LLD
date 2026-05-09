package org.example.entities.beverage;

import org.example.entities.inventory.IngredientType;

import java.util.Map;

public class Receipe {

    private Map<IngredientType, Double> ingredientsReq;

    public Receipe(Map<IngredientType, Double> ingredientsReq) {
        this.ingredientsReq = ingredientsReq;
    }

    public Map<IngredientType, Double> getIngredientsReq() {
        return ingredientsReq;
    }


}
