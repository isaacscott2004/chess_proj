package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.GameStatus;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO {
    private static final Collection<GameData> GAME_DATA_STORAGE = new ArrayList<>();
    private int gameCounter = 0;

    /**
     * gets all the current active games
     *
     * @return a Collection of all the active games
     */
    @Override
    public Collection<GameData> getListGames() {
        return GAME_DATA_STORAGE;
    }

    /**
     * Creates a game where the name is gameName and both of the usernames are initially null
     *
     * @param gameName The name of the game to create
     * @return the id of the game, id's start at 1
     */
    @Override
    public int createGame(String gameName) throws DataAccessException {
        if(gameName == null){
            throw new DataAccessException("gameName cannot be null");
        }
        GameData data = new GameData(gameCounter++, null, null, gameName, new ChessGame());
        GAME_DATA_STORAGE.add(data);
        return data.getGameID();
    }

    /**
     * checks to see if the inputted game id is valid
     *
     * @param gameID the id to be checked
     * @throws DataAccessException if there is no gameID that matches with the inputted id
     */
    @Override
    public void checkGameID(int gameID) throws DataAccessException {
        for (GameData data : GAME_DATA_STORAGE) {
            if (data.getGameID() == gameID) {
                return;
            }
        }
        throw new DataAccessException("There is no game with the specified gameID");

    }

    @Override
    public int getLargestGameID() {
        return getListGames().size();
    }

    @Override
    public ChessGame.TeamColor getPlayerColor(int gameID, String username) throws DataAccessException {
        ChessGame.TeamColor color = null;
        for(GameData data : GAME_DATA_STORAGE){
            if(data.getGameID() == gameID){
                if(data.getWhiteUsername().equals(username)){
                    color = ChessGame.TeamColor.WHITE;
                } else if(data.getBlackUsername().equals(username)){
                    color = ChessGame.TeamColor.BLACK;
                } else{
                    throw new DataAccessException("Game: " + data.getGameID() + " has no matching usernames");
                }
            }
        }
        if (color == null){
            throw new DataAccessException("There is no game with gameID: " + gameID);
        }
        return color;

    }

    @Override
    public void updateGameState(int gameID, ChessMove move) throws DataAccessException {
    }

    @Override
    public void resetPlayer(int gameID, String username) throws DataAccessException {

    }

    /**
     * Updates a games with a username and that username's color
     *
     * @param username    the username to be added to a game
     * @param playerColor the color that the username wants to be
     * @param gameID      finds the correct game to join
     * @throws DataAccessException if the specified color is already taken
     */
    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        for (GameData data : GAME_DATA_STORAGE) {
            if (data.getGameID() == gameID) {
                if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
                    if (data.getWhiteUsername() != null) {
                        throw new DataAccessException("White username already taken");
                    }
                    data.setWhiteUsername(username);
                } else {
                    if (data.getBlackUsername() != null) {
                        throw new DataAccessException("Black username already taken");
                    }
                    data.setBlackUsername(username);
                }
            }
        }
    }

    /**
     * Clears all GameData
     */
    @Override
    public void clearGameData() {
        GAME_DATA_STORAGE.clear();
        gameCounter = 0;

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateStatus(int gameID, GameStatus status) {

    }
}
