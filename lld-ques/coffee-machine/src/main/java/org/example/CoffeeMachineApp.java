package org.example;

import org.example.entities.beverage.BeverageType;
import org.example.entities.inventory.IngredientType;
import org.example.entities.mainmachine.CoffeeMachine;

public class CoffeeMachineApp {
    public static void main(String[] args) {

        CoffeeMachine coffeeMachine = new CoffeeMachine(8);
        coffeeMachine.dispenseBeverage(BeverageType.LATTE, "1234");
        coffeeMachine.addInventory(IngredientType.COFFEE_POWDER, 10000d);
        coffeeMachine.addInventory(IngredientType.HOT_WATER, 10000d);
        coffeeMachine.addInventory(IngredientType.MILK, 10000d);
        

        coffeeMachine.shutDown();

    }
}