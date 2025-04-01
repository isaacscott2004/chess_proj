package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.GameStatus;
import chess.InvalidMoveException;
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
     * gets the color of a player who is playing the game matching the gameID
     * @param gameID id to find game
     * @param username username to match
     * @return teamColor username is
     * @throws DataAccessException if gameId is not found or username is not in game;
     */
    ChessGame.TeamColor getPlayerColor(int gameID, String username) throws DataAccessException;

    /**
     * updates the state of the game
     * @param gameID the id of the game to update
     * @param move the change in the game
     * @throws DataAccessException if the game is not found
     * @throws InvalidMoveException if the move is not valid
     */
    void updateGameState(int gameID, ChessMove move) throws DataAccessException, InvalidMoveException;

    /**
     * resets username to null
     * @param gameID the game where the username should be reset
     * @param username the username to be reset
     * @throws DataAccessException if the game or the username is not found
     */
    void resetPlayer(int gameID, String username) throws DataAccessException;
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

    GameData getGame(int gameID);

    void updateStatus(int gameID, GameStatus status) throws DataAccessException;
}
