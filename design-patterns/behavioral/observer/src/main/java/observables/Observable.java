package observables;

import observer.Observer;

public interface Observable<T> {

    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();

    T getData();

}
