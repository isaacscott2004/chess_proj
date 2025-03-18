package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    /**
     * gets all the current active games
     *
     * @return a Collection of all the active games
     */
    Collection<GameData> getListGames() throws DataAccessException;

    /**
     * Creates a game where the name is gameName and both of the usernames are initially null
     *
     * @param gameName The name of the game to create
     * @return the id of the game, id's start at 1
     */
    int createGame(String gameName) throws DataAccessException;

    /**
     * checks to see if the inputted game id is valid
     *
     * @param gameID the id to be checked
     * @throws DataAccessException if there is no gameID that matches with the inputted id
     */
    void checkGameID(int gameID) throws DataAccessException;

    /**
     * gets the largest gameID
     * @return the largest gameID
     * @throws DataAccessException
     */
    int getLargestGameID() throws DataAccessException;

    /**
     * Updates a games with a username and that username's color
     *
     * @param username    the username to be added to a game
     * @param playerColor the color that the username wants to be
     * @param gameID      finds the correct game to join
     * @throws DataAccessException if the specified color is already taken
     */
    void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;

    /**
     * Clears all GameData
     */
    void clearGameData() throws DataAccessException;
}
