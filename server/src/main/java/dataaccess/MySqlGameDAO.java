package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO{
    @Override
    public Collection<GameData> getListGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public void checkGameID(int gameID) throws DataAccessException {

    }

    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {

    }

    @Override
    public void clearGameData() throws DataAccessException {

    }
}
