package org.example;

import observables.CricketScore;
import observables.WeatherData;
import observer.EmailAlert;
import observer.MobileAlert;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        WeatherData weatherData = new WeatherData();
        CricketScore cricketScore = new CricketScore();

        MobileAlert weatherMobileAlert = new MobileAlert(weatherData, "9999999999");
        MobileAlert cricketMobileAlert = new MobileAlert(cricketScore, "9999999999");
        EmailAlert emailWeatherAlert = new EmailAlert(weatherData, "abc@gmail.com");

        weatherData.setWeather(1,2,4);

        cricketScore.updateScore(12);

        weatherData.removeObserver(weatherMobileAlert);

        weatherData.setWeather(3,4,5);




    }
}