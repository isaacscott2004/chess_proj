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
import request.ListGamesRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.RegisterResult;

import java.util.ArrayList;
import java.util.Collection;

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
    public static void setUp() throws OtherException {
        userDataAccessObject = new MemoryUserDAO();
        authDataAccessObject = new MemoryAuthDAO();
        gameDataAccessObject = new MemoryGameDAO();
        userDataList = userDataAccessObject.getUserDataStorage();
        authDataList = authDataAccessObject.getAuthDataStorage();
        RegisterRequest registerRequestOne = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com",authDataAccessObject, userDataAccessObject);
        RegisterResult registerResultOne = UserService.register(registerRequestOne);
        authTokenOne = registerResultOne.authToken();

        RegisterRequest registerRequestTwo = new RegisterRequest("Messi", "Barcelona", "messi@gmail.com",authDataAccessObject, userDataAccessObject);
        RegisterResult registerResultTwo = UserService.register(registerRequestTwo);
        authTokenTwo = registerResultTwo.authToken();

        RegisterRequest registerRequestThree = new RegisterRequest("Ronaldo", "Madrid", "cr7@gmail.com",authDataAccessObject, userDataAccessObject);
        RegisterResult registerResultThree = UserService.register(registerRequestThree);
        authTokenThree = registerResultThree.authToken();

        RegisterRequest registerRequestFour = new RegisterRequest("Neymar", "Paris", "brazil@gmail.com",authDataAccessObject, userDataAccessObject);
        RegisterResult registerResultFour = UserService.register(registerRequestFour);
        authTokenFour = registerResultFour.authToken();


    }
    // helper methods
    void helperMethodOne() throws OtherException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne, authDataAccessObject, gameDataAccessObject);
        GameService.createGame(createGameRequestOne);

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Second game", authTokenTwo, authDataAccessObject, gameDataAccessObject);
        GameService.createGame(createGameRequestTwo);

        CreateGameRequest createGameRequestThree = new CreateGameRequest("Third game", authTokenThree, authDataAccessObject, gameDataAccessObject);
        GameService.createGame(createGameRequestThree);

        CreateGameRequest createGameRequestFour = new CreateGameRequest("Fourth game", authTokenFour, authDataAccessObject, gameDataAccessObject);
        GameService.createGame(createGameRequestFour);

    }
    ArrayList<GameData> helperMethodTwo() throws OtherException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenOne, authDataAccessObject, gameDataAccessObject);
        ListGamesResult listGamesResult = GameService.listGames(listGamesRequest);
        return (ArrayList<GameData>)listGamesResult.games();
    }

    @Test
    void preTest(){
        assertEquals(4, userDataList.size());
        assertEquals(4, authDataList.size());
    }

    @Test
    void createGameSuccess() throws OtherException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne, authDataAccessObject, gameDataAccessObject);
        CreateGameResult createGameResultOne = GameService.createGame(createGameRequestOne);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultOne.gameID());

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("second game", authTokenTwo, authDataAccessObject, gameDataAccessObject);
        CreateGameResult createGameResultTwo = GameService.createGame(createGameRequestTwo);
        assertEquals(2, gameDataAccessObject.getListGames().size());
        assertEquals(2, createGameResultTwo.gameID());
        gameDataAccessObject.clearGameData();

        CreateGameRequest createGameRequestThree = new CreateGameRequest("First game after clear", authTokenThree, authDataAccessObject, gameDataAccessObject);
        CreateGameResult createGameResultThree = GameService.createGame(createGameRequestThree);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultThree.gameID());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorNullParameters(){
        CreateGameRequest createGameRequestOne = new CreateGameRequest(null, authTokenOne, authDataAccessObject, gameDataAccessObject);
        assertThrows(OtherException.class, () -> GameService.createGame(createGameRequestOne));
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorInvalidAuthToken(){
        CreateGameRequest createGameRequestOne = new CreateGameRequest("test game", "1234", authDataAccessObject, gameDataAccessObject);
        assertThrows(UnauthorizedException.class, () -> GameService.createGame(createGameRequestOne));
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorGameNameTaken() throws OtherException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne, authDataAccessObject, gameDataAccessObject);
        GameService.createGame(createGameRequestOne);
        CreateGameRequest createGameRequestTwo = new CreateGameRequest("First game", authTokenTwo, authDataAccessObject, gameDataAccessObject);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertThrows(AlreadyTakenException.class, () -> GameService.createGame(createGameRequestTwo));
        assertEquals(1, gameDataAccessObject.getListGames().size());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());


    }

    @Test
    void listGamesSuccess() throws OtherException {
        helperMethodOne();
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenOne, authDataAccessObject, gameDataAccessObject);
        ListGamesResult listGamesResult = GameService.listGames(listGamesRequest);
        Collection<GameData> games = listGamesResult.games();
        ArrayList<GameData> gamesArray = (ArrayList<GameData>)games;
        assertEquals(4, games.size());
        int id = 1;
        ArrayList<String> expectedGameNames = new ArrayList<>();
        expectedGameNames.add("First game");
        expectedGameNames.add("Second game");
        expectedGameNames.add("Third game");
        expectedGameNames.add("Fourth game");
        for(int i = 0; i < gamesArray.size(); i++) {
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
    void listGamesErrorInvalidAuthToken() throws OtherException {
        helperMethodOne();
        ListGamesRequest listGamesRequest = new ListGamesRequest("1234", authDataAccessObject, gameDataAccessObject);
        assertThrows(UnauthorizedException.class, () -> GameService.listGames(listGamesRequest));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameSuccess() throws OtherException {
        helperMethodOne();
        ArrayList<GameData> games = helperMethodTwo();
        assertEquals("First game", games.getFirst().getGameName());
        assertEquals(1, games.getFirst().getGameID());
        assertNull(games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, ChessGame.TeamColor.BLACK, 1, authDataAccessObject, gameDataAccessObject);
        GameService.joinGame(joinGameRequestOne);
        assertEquals("Isaac",games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.WHITE, 1, authDataAccessObject, gameDataAccessObject);
        GameService.joinGame(joinGameRequestTwo);
        assertEquals("Isaac",games.getFirst().getBlackUsername());
        assertEquals("Messi",games.getFirst().getWhiteUsername());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorNullParameters() throws OtherException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, null, 1, authDataAccessObject, gameDataAccessObject);
        assertThrows(OtherException.class, () -> GameService.joinGame(joinGameRequestOne));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameErrorInvalidAuthToken() throws OtherException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest("1234", ChessGame.TeamColor.BLACK, 1, authDataAccessObject, gameDataAccessObject);
        assertThrows(UnauthorizedException.class, () -> GameService.joinGame(joinGameRequestOne));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorInvalidGameID() throws OtherException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.BLACK, 400, authDataAccessObject, gameDataAccessObject);
        assertThrows(OtherException.class, () -> GameService.joinGame(joinGameRequestOne));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameColorAlreadyTaken() throws OtherException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, ChessGame.TeamColor.BLACK, 1, authDataAccessObject, gameDataAccessObject);
        GameService.joinGame(joinGameRequestOne);
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(authTokenFour, ChessGame.TeamColor.BLACK, 1, authDataAccessObject, gameDataAccessObject);
        assertThrows(AlreadyTakenException.class, () -> GameService.joinGame(joinGameRequestTwo));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @AfterAll
    static void clear(){
        userDataAccessObject.clearUserData();
        authDataAccessObject.clearAuthdata();
        gameDataAccessObject.clearGameData();

    }


    }






