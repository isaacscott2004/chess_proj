package model;

import chess.ChessGame;

import java.util.Objects;

public class GameData {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;


    public GameData(int id, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = id ;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public GameData(){
        this.gameID = 0;
        this.whiteUsername = null;
        this.blackUsername = null;
        this.gameName = null;
        this.game = null;
    }


    public int getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public ChessGame getGame(){ return game;}

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public void setGameID(int id){ this.gameID = id; }

    public void setGame(ChessGame game){ this.game = game; }

    public void setGameName(String gameName){ this.gameName = gameName; }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameData gameData)) {
            return false;
        }
        return gameID == gameData.gameID && Objects.equals(whiteUsername, gameData.whiteUsername) &&
                Objects.equals(blackUsername, gameData.blackUsername) &&
                Objects.equals(gameName, gameData.gameName) &&
                Objects.equals(game, gameData.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public String toString() {
        return "GameData{" +
                "gameID=" + gameID +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                ", game=" + game +
                '}';
    }
}
