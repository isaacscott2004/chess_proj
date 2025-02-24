package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTests {
    private static Collection<AuthData> authDataList;
    private static Collection<UserData> userDataList;
    private static Collection<GameData> gameDataList;
    private static AuthDAO authAccessObject;
    private static UserDAO userAccessObject;
    private static GameDAO gameAccessObject;


    @BeforeAll
    static void setup() throws BadRequestException, DataAccessException {
        authAccessObject = new MemoryAuthDAO();
        userAccessObject = new MemoryUserDAO();
        gameAccessObject = new MemoryGameDAO();
        userDataList = userAccessObject.getUserDataStorage();
        authDataList = authAccessObject.getAuthDataStorage();
        gameDataList = gameAccessObject.getListGames();
        RegisterRequest registerRequestOne = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResultOne = UserService.register(registerRequestOne, authAccessObject, userAccessObject);
        String authTokenOne = registerResultOne.authToken();

        RegisterRequest registerRequestTwo = new RegisterRequest("Messi", "Barcelona", "messi@gmail.com");
        RegisterResult registerResultTwo = UserService.register(registerRequestTwo, authAccessObject, userAccessObject);
        String authTokenTwo = registerResultTwo.authToken();

        RegisterRequest registerRequestThree = new RegisterRequest("Ronaldo", "Madrid", "cr7@gmail.com");
        RegisterResult registerResultThree = UserService.register(registerRequestThree, authAccessObject, userAccessObject);
        String authTokenThree = registerResultThree.authToken();

        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game");
        GameService.createGame(createGameRequestOne, authTokenOne, authAccessObject, gameAccessObject);

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Second game");
        GameService.createGame(createGameRequestTwo, authTokenTwo, authAccessObject, gameAccessObject);

        CreateGameRequest createGameRequestThree = new CreateGameRequest("Third game");
        GameService.createGame(createGameRequestThree, authTokenThree, authAccessObject, gameAccessObject);

        JoinGameRequest joinGameRequestOne = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne, authTokenOne, authAccessObject, gameAccessObject);

        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        GameService.joinGame(joinGameRequestTwo, authTokenTwo, authAccessObject, gameAccessObject);

        JoinGameRequest joinGameRequestThree = new JoinGameRequest(ChessGame.TeamColor.BLACK, 2);
        GameService.joinGame(joinGameRequestThree, authTokenTwo, authAccessObject, gameAccessObject);

        JoinGameRequest joinGameRequestFour = new JoinGameRequest(ChessGame.TeamColor.WHITE, 2);
        GameService.joinGame(joinGameRequestFour, authTokenThree, authAccessObject, gameAccessObject);

    }

    @Test
    void testClear() throws DataAccessException {
        assertEquals(3, userDataList.size());
        assertEquals(3, gameDataList.size());
        assertEquals(3, authDataList.size());
        ClearService.clear(authAccessObject, userAccessObject, gameAccessObject);
        assertTrue(userDataList.isEmpty());
        assertTrue(gameDataList.isEmpty());
        assertTrue(authDataList.isEmpty());


    }


}
