package org.example.entities.inventory;

import java.util.Map;
import java.util.Objects;

public class InventoryObserver implements Observable.Observer {

    @Override
    public void update() {
        Map<IngredientType, Double> thresholdValueMap = Inventory.getInventoryInstance().getThresholdValueMap();
        for(Map.Entry<IngredientType, Double> thresholdValueMapEntry :  thresholdValueMap.entrySet()) {

            IngredientType reqIngredientType = thresholdValueMapEntry.getKey();
            Double reqValue = thresholdValueMapEntry.getValue();
            Double currentInventory = Inventory.getInventoryInstance().getInventory(reqIngredientType);

            if (Objects.isNull(currentInventory) || currentInventory < reqValue) {
                System.out.println(String.format("%s has less value %s", reqIngredientType, currentInventory));
            }

        }
    }
}
