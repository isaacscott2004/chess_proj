package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public Collection<GameData> getListGames()throws  DataAccessException;

    public int createGame(String gameName);

    public void checkGameID(int gameID) throws DataAccessException;

    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;

    public void clearGameData();
}
