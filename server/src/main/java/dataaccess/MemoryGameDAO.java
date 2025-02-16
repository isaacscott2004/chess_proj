package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO{
    private static final Collection<GameData> gameDataStorage = new ArrayList<>();

    @Override
    public Collection<GameData> getListGames(){
        return gameDataStorage;
    }

    @Override
    public int createGame(String gameName) {
        GameData data = new GameData(null, null, gameName, new ChessGame());
        gameDataStorage.add(data);
        return data.getGameID();
    }


    @Override
    public void checkGameID(int gameID) throws DataAccessException {
        for(GameData data: gameDataStorage){
            if(data.getGameID() == gameID){
                return;
            }
        }
        throw new DataAccessException("There is no game with the specified gameID");

    }

    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        for(GameData data : gameDataStorage){
            if(data.getGameID()==gameID){
                if(playerColor.equals(ChessGame.TeamColor.WHITE)){
                    if(data.getWhiteUsername() != null){
                        throw new DataAccessException("White username already taken");
                    }
                    data.setWhiteUsername(username);
                } else{
                    if(data.getBlackUsername() != null){
                        throw new DataAccessException("Black username already taken");
                    }
                    data.setBlackUsername(username);
                }
            }
        }
    }

    @Override
    public boolean isNameTaken(String gameName) {
        for(GameData data : gameDataStorage){
            if(data.getGameName().equals(gameName)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void clearGameData() {
        gameDataStorage.clear();
        GameData.resetGameIDCounter();

    }
}
