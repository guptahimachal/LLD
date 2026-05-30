package org.example;

import org.example.model.Game;

import java.lang.reflect.InvocationTargetException;


public class Main {
    public static void main(String[] args) {

        Game game = new Game();

        try {
            game.startGame();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }



    }
}