package org.example.entities.exception;

import org.example.entities.inventory.IngredientType;

public class InsufficientIngredientException extends RuntimeException {

    private IngredientType requiredIngredient;
    private Double requiredValue;
    private Double availableValue;

    public InsufficientIngredientException(IngredientType requiredIngredient, Double requiredValue, Double availableValue) {
        this.requiredIngredient = requiredIngredient;
        this.requiredValue = requiredValue;
        this.availableValue = availableValue;
    }

    @Override
    public String getMessage() {
        return String.format("Insufficient qty = %s, found for ingredient = %s, required = %s", availableValue, requiredIngredient, requiredIngredient);
    }

}
