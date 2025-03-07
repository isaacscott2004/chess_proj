package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.RegisterResult;
import server.Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private static String authTokenOne;
    private static String authTokenTwo;
    private static String authTokenThree;
    private static String authTokenFour;
    private static GameDAO gameDataAccessObject;
    private static UserDAO userDataAccessObject;
    private static AuthDAO authDataAccessObject;
    private static Collection<AuthData> authDataList;
    private static Collection<UserData> userDataList;



    @BeforeAll
    public static void setUp() throws Exception {
        HashMap<String, Object> daos = ServiceTestUtilities.chooseMemoryType(Server.MemoryType.SQL_MEMORY);
        gameDataAccessObject = (GameDAO) daos.get("game");
        userDataAccessObject = (UserDAO) daos.get("user");
        authDataAccessObject = (AuthDAO) daos.get("auth");
        authDataAccessObject.clearAuthdata();
        userDataAccessObject.clearUserData();
        gameDataAccessObject.clearGameData();
        userDataList = userDataAccessObject.getUserDataStorage();
        authDataList = authDataAccessObject.getAuthDataStorage();
        RegisterRequest registerRequestOne = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResultOne = UserService.register(registerRequestOne, authDataAccessObject, userDataAccessObject);
        authTokenOne = registerResultOne.authToken();

        RegisterRequest registerRequestTwo = new RegisterRequest("Messi", "Barcelona", "messi@gmail.com");
        RegisterResult registerResultTwo = UserService.register(registerRequestTwo, authDataAccessObject, userDataAccessObject);
        authTokenTwo = registerResultTwo.authToken();

        RegisterRequest registerRequestThree = new RegisterRequest("Ronaldo", "Madrid", "cr7@gmail.com");
        RegisterResult registerResultThree = UserService.register(registerRequestThree, authDataAccessObject, userDataAccessObject);
        authTokenThree = registerResultThree.authToken();

        RegisterRequest registerRequestFour = new RegisterRequest("Neymar", "Paris", "brazil@gmail.com");
        RegisterResult registerResultFour = UserService.register(registerRequestFour, authDataAccessObject, userDataAccessObject);
        authTokenFour = registerResultFour.authToken();


    }

    // helper methods
    void helperMethodOne() throws BadRequestException, DataAccessException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game");
        GameService.createGame(createGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Second game");
        GameService.createGame(createGameRequestTwo, authTokenTwo, authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestThree = new CreateGameRequest("Third game");
        GameService.createGame(createGameRequestThree, authTokenThree, authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestFour = new CreateGameRequest("Fourth game");
        GameService.createGame(createGameRequestFour, authTokenFour, authDataAccessObject, gameDataAccessObject);

    }

    ArrayList<GameData> helperMethodTwo() throws DataAccessException {
        ListGamesResult listGamesResult = GameService.listGames(authTokenOne, authDataAccessObject, gameDataAccessObject);
        return (ArrayList<GameData>) listGamesResult.games();
    }

    @Test
    void preTest() throws DataAccessException {
        userDataList = userDataAccessObject.getUserDataStorage();
        authDataList = authDataAccessObject.getAuthDataStorage();
        assertEquals(4, userDataList.size());
        assertEquals(4, authDataList.size());
    }

    @Test
    void createGameSuccess() throws BadRequestException, DataAccessException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game");
        CreateGameResult createGameResultOne = GameService.createGame(createGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultOne.gameID());

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("second game");
        CreateGameResult createGameResultTwo = GameService.createGame(createGameRequestTwo, authTokenTwo, authDataAccessObject, gameDataAccessObject);
        assertEquals(2, gameDataAccessObject.getListGames().size());
        assertEquals(2, createGameResultTwo.gameID());
        gameDataAccessObject.clearGameData();

        CreateGameRequest createGameRequestThree = new CreateGameRequest("First game after clear");
        CreateGameResult createGameResultThree =
                GameService.createGame(createGameRequestThree, authTokenThree, authDataAccessObject, gameDataAccessObject);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultThree.gameID());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorNullParameters() {
        CreateGameRequest createGameRequestOne = new CreateGameRequest(null);
        assertThrows(BadRequestException.class, () ->
                GameService.createGame(createGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject));
    }

    @Test
    void createGameErrorInvalidAuthToken() throws DataAccessException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("test game");
        assertThrows(UnauthorizedException.class, () ->
                GameService.createGame(createGameRequestOne, "1234", authDataAccessObject, gameDataAccessObject));
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }


    @Test
    void listGamesSuccess() throws BadRequestException, DataAccessException {
        helperMethodOne();
        ListGamesResult listGamesResult = GameService.listGames(authTokenOne, authDataAccessObject, gameDataAccessObject);
        Collection<GameData> games = listGamesResult.games();
        ArrayList<GameData> gamesArray = (ArrayList<GameData>) games;
        assertEquals(4, games.size());
        int id = 1;
        ArrayList<String> expectedGameNames = new ArrayList<>();
        expectedGameNames.add("First game");
        expectedGameNames.add("Second game");
        expectedGameNames.add("Third game");
        expectedGameNames.add("Fourth game");
        for (int i = 0; i < gamesArray.size(); i++) {
            GameData currentGame = gamesArray.get(i);
            assertEquals(id, currentGame.getGameID());
            assertEquals(expectedGameNames.get(i), currentGame.getGameName());
            assertNull(currentGame.getBlackUsername());
            assertNull(currentGame.getWhiteUsername());
            id++;
        }
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void listGamesErrorInvalidAuthToken() throws BadRequestException, DataAccessException {
        helperMethodOne();
        assertThrows(UnauthorizedException.class, () -> GameService.listGames("1234", authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameSuccess() throws BadRequestException, DataAccessException {
        helperMethodOne();
        ArrayList<GameData> games = helperMethodTwo();
        assertEquals("First game", games.getFirst().getGameName());
        assertEquals(1, games.getFirst().getGameID());
        assertNull(games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject);
        games = (ArrayList<GameData>) gameDataAccessObject.getListGames();
        assertEquals("Isaac", games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        GameService.joinGame(joinGameRequestTwo, authTokenTwo, authDataAccessObject, gameDataAccessObject);
        games = (ArrayList<GameData>) gameDataAccessObject.getListGames();
        assertEquals("Isaac", games.getFirst().getBlackUsername());
        assertEquals("Messi", games.getFirst().getWhiteUsername());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorNullParameters() throws BadRequestException, DataAccessException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(null, 1);
        assertThrows(BadRequestException.class, () ->
                GameService.joinGame(joinGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameErrorInvalidAuthToken() throws BadRequestException, DataAccessException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1);
        assertThrows(UnauthorizedException.class, () ->
                GameService.joinGame(joinGameRequestOne, "1234", authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorInvalidGameID() throws BadRequestException, DataAccessException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(ChessGame.TeamColor.BLACK, 400);
        assertThrows(BadRequestException.class, () ->
                GameService.joinGame(joinGameRequestOne, authTokenTwo, authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameColorAlreadyTaken() throws BadRequestException, DataAccessException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne, authTokenOne, authDataAccessObject, gameDataAccessObject);
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(ChessGame.TeamColor.BLACK, 1);
        assertThrows(AlreadyTakenException.class, () ->
                GameService.joinGame(joinGameRequestTwo, authTokenTwo, authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @AfterAll
    static void clear() throws DataAccessException {
        userDataAccessObject.clearUserData();
        authDataAccessObject.clearAuthdata();
        gameDataAccessObject.clearGameData();

    }




}






