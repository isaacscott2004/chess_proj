package client.managers;

import chess.ChessBoard;
import chess.ChessGame;

public class GameManager {
    private static ChessBoard board;
    private static ChessGame.TeamColor color;
    private static ChessGame game;

    public static ChessBoard getBoard(){
        return board;
    }

    public static ChessGame.TeamColor getColor(){
        return color;
    }

    public static ChessGame getGame(){
        return game;
    }

    public static void setBoard(ChessBoard otherBoard){
        board = otherBoard;

    }
    public static void setColor(ChessGame.TeamColor otherColor){
        color = otherColor;
    }

    public static void setGame(ChessGame game) {
        GameManager.game = game;
    }



}

