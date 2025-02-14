package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public Collection<GameData> getListGames()throws  DataAccessException;

    public int createGame(String gameName);

    public int getGameID(String gameName) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException, IllegalArgumentException;

    public void clearGameData();
}
