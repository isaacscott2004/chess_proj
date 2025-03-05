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
    private static GameDAO gameDataAccessObject;
    private static UserDAO userDataAccessObject;
    private static AuthDAO authDataAccessObject;

    @BeforeAll
    static void setup() throws Exception {
        ClearServiceTests.chooseMemoryType(false);
        userDataAccessObject.clearUserData();
        authDataAccessObject.clearAuthdata();
        gameDataAccessObject.clearGameData();
        RegisterRequest registerRequestOne = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResultOne = UserService.register(registerRequestOne, authDataAccessObject, userDataAccessObject);
        String authTokenOne = registerResultOne.authToken();

        RegisterRequest registerRequestTwo = new RegisterRequest("Messi", "Barcelona", "messi@gmail.com");
        RegisterResult registerResultTwo = UserService.register(registerRequestTwo, authDataAccessObject, userDataAccessObject);
        String authTokenTwo = registerResultTwo.authToken();

        RegisterRequest registerRequestThree = new RegisterRequest("Ronaldo", "Madrid", "cr7@gmail.com");
        RegisterResult registerResultThree = UserService.register(registerRequestThree, authDataAccessObject, userDataAccessObject);
        String authTokenThree = registerResultThree.authToken();

        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game");
        GameService.createGame(createGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Second game");
        GameService.createGame(createGameRequestTwo, authTokenTwo, authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestThree = new CreateGameRequest("Third game");
        GameService.createGame(createGameRequestThree, authTokenThree, authDataAccessObject, gameDataAccessObject);

        JoinGameRequest joinGameRequestOne = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject);

        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        GameService.joinGame(joinGameRequestTwo, authTokenTwo, authDataAccessObject, gameDataAccessObject);

        JoinGameRequest joinGameRequestThree = new JoinGameRequest(ChessGame.TeamColor.BLACK, 2);
        GameService.joinGame(joinGameRequestThree, authTokenTwo, authDataAccessObject, gameDataAccessObject);

        JoinGameRequest joinGameRequestFour = new JoinGameRequest(ChessGame.TeamColor.WHITE, 2);
        GameService.joinGame(joinGameRequestFour, authTokenThree, authDataAccessObject, gameDataAccessObject);

        authDataList = authDataAccessObject.getAuthDataStorage();
        userDataList = userDataAccessObject.getUserDataStorage();
        gameDataList = gameDataAccessObject.getListGames();

    }

    @Test
    void testClear() throws DataAccessException {
        assertEquals(3, userDataList.size());
        assertEquals(3, gameDataList.size());
        assertEquals(3, authDataList.size());
        ClearService.clear(authDataAccessObject, userDataAccessObject, gameDataAccessObject);
        authDataList = authDataAccessObject.getAuthDataStorage();
        userDataList = userDataAccessObject.getUserDataStorage();
        gameDataList = gameDataAccessObject.getListGames();
        assertTrue(userDataList.isEmpty());
        assertTrue(gameDataList.isEmpty());
        assertTrue(authDataList.isEmpty());


    }

    private static void chooseMemoryType(boolean inMemory){
        if(inMemory){
            userDataAccessObject = new MemoryUserDAO();
            authDataAccessObject = new MemoryAuthDAO();
            gameDataAccessObject = new MemoryGameDAO();
        }
        else{
            userDataAccessObject = new MySqlUserDAO();
            authDataAccessObject = new MySqlAuthDAO();
            gameDataAccessObject = new MySqlGameDAO();
        }

    }


}
