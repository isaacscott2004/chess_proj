package client;

import chess.ChessGame;

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

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return number + " : " +gameName + ", WHITE: " + whiteUsername + ", BLACK: " + blackUsername;
    }
}
