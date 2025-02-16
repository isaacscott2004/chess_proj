package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DAOImplmentation;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.ClearRequest;
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

    @BeforeAll
    static void setup(){
        UserDAO userDataAccessObject = DAOImplmentation.getUserDAO();
        AuthDAO authDataAccessObject = DAOImplmentation.getAuthDAO();
        GameDAO gameDataAccessObject = DAOImplmentation.getGameDAO();
        userDataList = userDataAccessObject.getUserDataStorage();
        authDataList = authDataAccessObject.getAuthDataStorage();
        gameDataList = gameDataAccessObject.getListGames();
        RegisterRequest registerRequestOne = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResultOne = UserService.register(registerRequestOne);
        String authTokenOne = registerResultOne.authToken();

        RegisterRequest registerRequestTwo = new RegisterRequest("Messi", "Barcelona", "messi@gmail.com");
        RegisterResult registerResultTwo = UserService.register(registerRequestTwo);
        String authTokenTwo = registerResultTwo.authToken();

        RegisterRequest registerRequestThree = new RegisterRequest("Ronaldo", "Madrid", "cr7@gmail.com");
        RegisterResult registerResultThree = UserService.register(registerRequestThree);
        String authTokenThree = registerResultThree.authToken();

        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne);
        GameService.createGame(createGameRequestOne);

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Second game", authTokenTwo);
        GameService.createGame(createGameRequestTwo);

        CreateGameRequest createGameRequestThree = new CreateGameRequest("Third game", authTokenThree);
        GameService.createGame(createGameRequestThree);

        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne);

        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.WHITE, 1);
        GameService.joinGame(joinGameRequestTwo);

        JoinGameRequest joinGameRequestThree = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.BLACK, 2);
        GameService.joinGame(joinGameRequestThree);

        JoinGameRequest joinGameRequestFour = new JoinGameRequest(authTokenThree, ChessGame.TeamColor.WHITE, 2);
        GameService.joinGame(joinGameRequestFour);

    }

    @Test
    void testClear(){
        assertEquals(3, userDataList.size());
        assertEquals(3, gameDataList.size());
        assertEquals(3, authDataList.size());
        ClearRequest clearRequest = new ClearRequest();
        ClearService.clear(clearRequest);
        assertTrue(userDataList.isEmpty());
        assertTrue(gameDataList.isEmpty());
        assertTrue(authDataList.isEmpty());





    }


}
