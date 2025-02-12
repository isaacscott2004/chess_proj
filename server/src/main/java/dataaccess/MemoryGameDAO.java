package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    @Override
    public Collection<GameData> getListGames(String username) {
        return List.of();
    }

    @Override
    public GameData createGame(String username, String gameName) {
        return null;
    }

    @Override
    public int getGameID(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public boolean checkIfColorAvailable(String playerColor, int gameID) {
        return false;
    }

    @Override
    public void updateGame(String username, String playerColor, int gameID) {

    }

    @Override
    public void clearGameData() {

    }
}
