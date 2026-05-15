package observables;

import observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class  WeatherData implements Observable<String> {

    private List<Observer> observerList = new ArrayList<>();

    private int temp;
    private int humidity;
    private int rainPercentage;

    public void setWeather(int temp, int humidity, int rainPercentage) {

        this.temp = temp;
        this.humidity = humidity;
        this.rainPercentage = rainPercentage;

        notifyObservers();
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
    public void notifyObservers() {

        for(Observer observer : observerList) {
            observer.update();
        }

    }

    @Override
    public String getData() {
        return String.format("Temp is %s, Humidity is %s, Rain percentage is %s", temp, rainPercentage, humidity);
    }
}
