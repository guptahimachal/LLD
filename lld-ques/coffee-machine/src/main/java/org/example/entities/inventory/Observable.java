package org.example.entities.inventory;

public interface Observable {

    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObserver();

    interface Observer {

        void update();

    }


}
