package org.example.entities.mainmachine;

import org.example.entities.beverage.Beverage;
import org.example.entities.beverage.BeverageFactory;
import org.example.entities.beverage.BeverageType;
import org.example.entities.exception.BeverageNotSupportedException;
import org.example.entities.exception.InsufficientIngredientException;
import org.example.entities.inventory.IngredientType;
import org.example.entities.inventory.Inventory;
import org.example.entities.inventory.InventoryObserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoffeeMachine {

    private Inventory inventory;
    private ExecutorService executorService;


    public CoffeeMachine(int maxParallelTask) {
        this.inventory = Inventory.getInventoryInstance();
        this.executorService = Executors.newFixedThreadPool(maxParallelTask);
        inventory.addObserver(new InventoryObserver());
    }


    public void dispenseBeverage(BeverageType beverageType, String requestId) {
        executorService.submit(() -> {
            try {
                Beverage beverage = BeverageFactory.getBeverage(beverageType);
                inventory.consumeInventory(beverage.getReceipe().getIngredientsReq());
                System.out.println(String.format("[%s] %s is served", requestId, beverageType));
            } catch (BeverageNotSupportedException | InsufficientIngredientException knownException) {
                System.out.println(String.format("[%s] Cannot dispense [%s] due to [error = %s], Please select another Beverage", requestId, beverageType, knownException.getMessage()));
            } catch (Exception ex) {
                System.out.println(String.format("[%s] Please contact admin [error = %s] [trace = %s]", requestId, ex.getMessage(), ex.getStackTrace()));
            }
        });
    }

    public void addInventory(IngredientType ingredientType, Double qty) {
        inventory.addInventory(ingredientType, qty);
    }

    public void shutDown() {
        executorService.shutdown();
    }

}
