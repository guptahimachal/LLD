package org.example.entities.inventory;

import org.example.entities.exception.InsufficientIngredientException;

import java.util.*;

public class Inventory implements Observable {

    private List<Observer> observerList;
    private Map<IngredientType, Double> inventoryMap;
    private Map<IngredientType, Double> thresholdValueMap;

    private static Inventory inventoryInstance;

    private Inventory() {
        this.inventoryMap = new HashMap<>();
        this.observerList = new ArrayList<>();
        this.thresholdValueMap = new HashMap<>();
    }

    public static Inventory getInventoryInstance() {
        if (Objects.isNull(inventoryInstance)) {
            synchronized (Inventory.class) {
                if (Objects.isNull(inventoryInstance)) {
                    inventoryInstance = new Inventory();
                }
            }
        }
        return inventoryInstance;
    }

    public void setThresholdValue(IngredientType ingredientType, Double thresholdVal) {
        thresholdValueMap.put(ingredientType, thresholdVal);
    }

    public Map<IngredientType, Double> getThresholdValueMap() {
        return thresholdValueMap;
    }

    public Double getInventory(IngredientType ingredientType) {
        return inventoryMap.getOrDefault(ingredientType, null);
    }

    public void addInventory(IngredientType ingredientType, Double qty) {
        if (!inventoryMap.containsKey(ingredientType)) {
            inventoryMap.put(ingredientType, 0d);
        }
        inventoryMap.put(ingredientType, inventoryMap.get(ingredientType) + qty);
    }

    private void verifyInventory(Map<IngredientType, Double> requiredInventory) {
        for(Map.Entry<IngredientType, Double> entry : requiredInventory.entrySet()) {
            IngredientType requiredIngredient = entry.getKey();
            Double requiredValue = entry.getValue();

            if (!inventoryMap.containsKey(requiredIngredient) || inventoryMap.get(requiredIngredient) < requiredValue) {
                throw new InsufficientIngredientException(requiredIngredient, requiredValue, inventoryMap.get(requiredIngredient));
            }
        }
    }

    public void consumeInventory(Map<IngredientType, Double> requiredInventory) {
        verifyInventory(requiredInventory);
        for(Map.Entry<IngredientType, Double> entry : requiredInventory.entrySet()) {
            IngredientType requiredIngredient = entry.getKey();
            Double requiredValue = entry.getValue();
            inventoryMap.put(requiredIngredient, inventoryMap.get(requiredIngredient) - requiredValue);
        }
        notifyObserver();
    }


    @Override
    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObserver() {
        observerList.forEach(Observer::update);
    }
}
