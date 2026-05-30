package org.example.model;

import org.example.exception.InvalidMoveException;
import org.example.model.board.Board;
import org.example.model.player.Player;
import org.example.model.player.PlayerFactory;
import org.example.model.playerpiece.PlayerPiece;

import java.util.*;

public class Game {

    private Board board;

    private final Deque<Player> playersQueue;

    private static final Scanner scanner = new Scanner(System.in);

    public Game() {

        System.out.println("Enter board size ");
        int size = scanner.nextInt();

        System.out.println("Enter num Players ");
        int numPlayers = scanner.nextInt();

        this.board = new Board(size);
        playersQueue = new LinkedList<>();

        scanner.nextLine();
        List<Player> players = PlayerFactory.givePlayers(numPlayers);
        playersQueue.addAll(players);
    }

    public void startGame() {

        Player winner = null;

        while(true) {

            board.printBoard();

            if (!board.isFreeSpaceLeft()) {
                break;
            }

            Player currentPlayer = playersQueue.peek();
            playersQueue.pop();

            System.out.println(String.format("Player - %s, Enter your move", currentPlayer.getName()));
            int moveX = scanner.nextInt();
            int moveY = scanner.nextInt();

            try {
                board.addPiece(moveX, moveY, currentPlayer.getPlayerPiece());
            } catch (InvalidMoveException invalidMoveException) {
                System.out.println(String.format("Invalid move by %s , Please try again", currentPlayer.getName()));
                playersQueue.addFirst(currentPlayer);
                continue;
            }

            boolean doesPlayerWin = board.isWinnableMove(moveX, moveY, currentPlayer.getPlayerPiece());

            if (doesPlayerWin) {
                winner = currentPlayer;
                break;
            }

            playersQueue.add(currentPlayer);

        }

        if (Objects.nonNull(winner)) {
            System.out.println(String.format("Winner is %s", winner.getName()));
        } else {
            System.out.println("Game is Tie");
        }
        board.printBoard();
    }




}
