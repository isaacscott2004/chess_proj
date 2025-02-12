package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    public Collection<GameData> getListGames(String username);

    public GameData createGame(String username, String gameName);

    public int getGameID(String gameName);

    public GameData getGame(int gameID);

    public boolean checkIfColorAvailable(String playerColor, int gameID);

    public void updateGame(String username, String playerColor, int gameID);

    public void clearGameData();
}
