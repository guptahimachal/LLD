package org.example.model.player;

import org.example.model.playerpiece.PlayerPiece;

import java.util.*;

public class PlayerFactory {

    private static final Map<String, Player> playersByName = new HashMap<>();
    private static final Map<Character, Player> playersByChar = new HashMap<>();

    public static List<Player> givePlayers(int numPlayers) {

        Scanner scanner = new Scanner(System.in);

        List<Player> players = new ArrayList<>();

        for (int i=0 ; i<numPlayers ; ) {

            System.out.println("Enter Player " + (i+1) + " name");
            String playerName = scanner.nextLine();

            if (playersByName.containsKey(playerName)) {
                System.out.println("Name already taken, Please try again");
                continue;
            }

            System.out.println("Enter Player " + (i+1) + " symbol");
            String symbolString = scanner.nextLine();

            if (symbolString == null || symbolString.length() != 1 || playersByChar.containsKey(symbolString.charAt(0))) {
                System.out.println("Invalid symbol, Please try again");
                continue;
            }

            Player player = new Player(playerName, new PlayerPiece(symbolString.charAt(0)));
            players.add(player);
            playersByName.put(player.getName(), player);
            playersByChar.put(player.getPlayerPiece().getSymbol(), player);
            i++;
        }
        return players;
    }
}
