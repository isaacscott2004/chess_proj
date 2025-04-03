package client;

import chess.ChessBoard;
import chess.ChessGame;

public class GameManager {
    private static ChessBoard board;
    private static ChessGame.TeamColor color;

    public static ChessBoard getBoard(){
        return board;
    }

    public static ChessGame.TeamColor getColor(){
        return color;
    }

    public static void setBoard(ChessBoard otherBoard){
        board = otherBoard;

    }
    public static void setColor(ChessGame.TeamColor otherColor){
        color = otherColor;
    }


    public static void clearBoard(){
        board = null;
    }

    public static void clearColor(){
        color = null;
    }


}

