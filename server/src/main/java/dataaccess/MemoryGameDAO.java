package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO{
    private static final Collection<GameData> gameDataStorage = new ArrayList<>();

    @Override
    public Collection<GameData> getListGames()throws  DataAccessException{
        if (gameDataStorage.isEmpty()){
            throw new DataAccessException("There are no games");
        }
        return gameDataStorage;
    }

    @Override
    public GameData createGame(String gameName) {
        GameData data = new GameData(null, null, gameName, new ChessGame());
        gameDataStorage.add(data);
        return data;
    }

    @Override
    public int getGameID(String gameName) throws DataAccessException {
        for(GameData data: gameDataStorage){
            if(data.getGameName().equals(gameName)){
                return data.getGameID();
            }
        }
        throw  new DataAccessException("There is no game with the specified game name");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for(GameData data: gameDataStorage){
            if(data.getGameID() == gameID){
                return data;
            }
        }
        throw new DataAccessException("There is no game with the specified gameID");
    }

    @Override
    public void updateGame(String username, String playerColor, int gameID) throws DataAccessException, IllegalArgumentException {
        if(!(playerColor.equals(ChessGame.TeamColor.BLACK.toString()) || playerColor.equals(ChessGame.TeamColor.WHITE.toString()))){
            throw new IllegalArgumentException("playerColor must be BLACK or WHITE");
        }
        for(GameData data : gameDataStorage){
            if(data.getGameID()==gameID){
                if(playerColor.equals(ChessGame.TeamColor.WHITE.toString())){
                    data.setWhiteUsername(username);
                } else{
                    data.setBlackUsername(username);
                }
            }
            throw new DataAccessException("There is no game with the specified gameID");
        }
    }

    @Override
    public void clearGameData() {
        gameDataStorage.clear();
    }
}
