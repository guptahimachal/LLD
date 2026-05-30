package org.example.model.board;

import org.example.exception.InvalidMoveException;
import org.example.model.player.Player;
import org.example.model.playerpiece.PlayerPiece;

public class Board {

    private final int size;

    private final PlayerPiece[][] board;

    private int totalMoves;

    public Board(int size) {
        this.size = size;
        board = new PlayerPiece[size][size];
        totalMoves = size * size;
    }

    public void addPiece(int x, int y, PlayerPiece playerPiece) {
        if (x < 0 || x >= size || y < 0 || y >= size || board[x][y] != null) {
            throw new InvalidMoveException();
        }
        board[x][y] = playerPiece;
        totalMoves--;
    }

    public void printBoard() {
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++) {
                System.out.printf(" " + (board[i][j] == null ? ' ' : board[i][j].getSymbol()) + " |");
            }
            System.out.print("\n");
        }
    }

    public boolean isFreeSpaceLeft() {
        return totalMoves != 0;
    }

    public boolean isWinnableMove(int moveX, int moveY, PlayerPiece playerPiece) {
        if (isLineFilled(moveX, 0, 0, 1, playerPiece.getSymbol())) {
            return true;
        }
        if (isLineFilled(0, moveY, 1, 0, playerPiece.getSymbol())) {
            return true;
        }
        if (moveX == moveY && isLineFilled(0, 0, 1, 1, playerPiece.getSymbol())) {
            return true;
        }
        if (moveY + moveX == size-1 && isLineFilled(0, size-1, 1, -1, playerPiece.getSymbol()) ) {
            return true;
        }
        return false;
    }

    private boolean isLineFilled(int startX, int startY, int incX, int incY, Character symbol) {

        for(int i=0 ; i<size ; i++) {
            int newX = startX + i * incX;
            int newY = startY + i * incY;
            if (board[newX][newY] == null || !board[newX][newY].getSymbol().equals(symbol)) {
                return false;
            }
        }
        return true;
    }
}
