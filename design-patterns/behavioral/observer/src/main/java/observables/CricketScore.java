package observables;

import observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class CricketScore implements Observable<String> {

    private List<Observer> observers = new ArrayList<>();

    private int score;
    private int target;

    public void updateScore(int score) {
        if (this.score != score) {
            this.score = score;
            notifyObservers();
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }

    @Override
    public String getData() {
        return String.format("Score is %s, target is %s", score, target);
    }
}
