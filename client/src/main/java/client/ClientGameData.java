package client;

import chess.ChessGame;

import java.util.HashMap;

public class ClientGameData {
    private int number;
    private int id;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    public ClientGameData(int number, int id, String whiteUsername, String blackUsername, String gameName){
        this.number = number;
        this.id = id;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }

    @Override
    public String toString() {
        return number + " : " +gameName + ", WHITE: " + whiteUsername + ", BLACK: " + blackUsername;
    }
}
