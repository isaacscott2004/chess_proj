package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    Collection<GameData> getListGames();

    int createGame(String gameName);

    void checkGameID(int gameID) throws DataAccessException;

    void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;

    boolean isNameTaken(String gameName);

    void clearGameData();
}
