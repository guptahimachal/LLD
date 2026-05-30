package org.example.model.player;

import org.example.model.playerpiece.PlayerPiece;

public class Player {

    private final String name;

    private final PlayerPiece playerPiece;

    public Player(String name, PlayerPiece playerPiece) {
        this.name = name;
        this.playerPiece = playerPiece;
    }


    public String getName() {
        return name;
    }

    public PlayerPiece getPlayerPiece() {
        return playerPiece;
    }
}
