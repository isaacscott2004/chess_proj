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
    public static void setUp() throws BadRequestException {
        userDataAccessObject = new MemoryUserDAO();
        authDataAccessObject = new MemoryAuthDAO();
        gameDataAccessObject = new MemoryGameDAO();
        userDataList = userDataAccessObject.getUserDataStorage();
        authDataList = authDataAccessObject.getAuthDataStorage();
        RegisterRequest registerRequestOne = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResultOne = UserService.register(registerRequestOne,authDataAccessObject, userDataAccessObject);
        authTokenOne = registerResultOne.authToken();

        RegisterRequest registerRequestTwo = new RegisterRequest("Messi", "Barcelona", "messi@gmail.com");
        RegisterResult registerResultTwo = UserService.register(registerRequestTwo,authDataAccessObject, userDataAccessObject);
        authTokenTwo = registerResultTwo.authToken();

        RegisterRequest registerRequestThree = new RegisterRequest("Ronaldo", "Madrid", "cr7@gmail.com");
        RegisterResult registerResultThree = UserService.register(registerRequestThree,authDataAccessObject, userDataAccessObject);
        authTokenThree = registerResultThree.authToken();

        RegisterRequest registerRequestFour = new RegisterRequest("Neymar", "Paris", "brazil@gmail.com");
        RegisterResult registerResultFour = UserService.register(registerRequestFour,authDataAccessObject, userDataAccessObject);
        authTokenFour = registerResultFour.authToken();


    }
    // helper methods
    void helperMethodOne() throws BadRequestException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne);
        GameService.createGame(createGameRequestOne ,authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Second game", authTokenTwo);
        GameService.createGame(createGameRequestTwo, authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestThree = new CreateGameRequest("Third game", authTokenThree);
        GameService.createGame(createGameRequestThree, authDataAccessObject, gameDataAccessObject);

        CreateGameRequest createGameRequestFour = new CreateGameRequest("Fourth game", authTokenFour);
        GameService.createGame(createGameRequestFour, authDataAccessObject, gameDataAccessObject);

    }
    ArrayList<GameData> helperMethodTwo() throws BadRequestException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenOne);
        ListGamesResult listGamesResult = GameService.listGames(listGamesRequest, authDataAccessObject, gameDataAccessObject);
        return (ArrayList<GameData>)listGamesResult.games();
    }

    @Test
    void preTest(){
        assertEquals(4, userDataList.size());
        assertEquals(4, authDataList.size());
    }

    @Test
    void createGameSuccess() throws BadRequestException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne);
        CreateGameResult createGameResultOne = GameService.createGame(createGameRequestOne,authDataAccessObject, gameDataAccessObject);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultOne.gameID());

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("second game", authTokenTwo);
        CreateGameResult createGameResultTwo = GameService.createGame(createGameRequestTwo, authDataAccessObject, gameDataAccessObject);
        assertEquals(2, gameDataAccessObject.getListGames().size());
        assertEquals(2, createGameResultTwo.gameID());
        gameDataAccessObject.clearGameData();

        CreateGameRequest createGameRequestThree = new CreateGameRequest("First game after clear", authTokenThree);
        CreateGameResult createGameResultThree = GameService.createGame(createGameRequestThree,authDataAccessObject, gameDataAccessObject);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultThree.gameID());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorNullParameters(){
        CreateGameRequest createGameRequestOne = new CreateGameRequest(null, authTokenOne);
        assertThrows(BadRequestException.class, () -> GameService.createGame(createGameRequestOne, authDataAccessObject, gameDataAccessObject));
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorInvalidAuthToken(){
        CreateGameRequest createGameRequestOne = new CreateGameRequest("test game", "1234");
        assertThrows(UnauthorizedException.class, () -> GameService.createGame(createGameRequestOne, authDataAccessObject, gameDataAccessObject));
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorGameNameTaken() throws BadRequestException {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne);
        GameService.createGame(createGameRequestOne, authDataAccessObject, gameDataAccessObject);
        CreateGameRequest createGameRequestTwo = new CreateGameRequest("First game", authTokenTwo);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertThrows(AlreadyTakenException.class, () -> GameService.createGame(createGameRequestTwo, authDataAccessObject, gameDataAccessObject));
        assertEquals(1, gameDataAccessObject.getListGames().size());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());


    }

    @Test
    void listGamesSuccess() throws BadRequestException {
        helperMethodOne();
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenOne);
        ListGamesResult listGamesResult = GameService.listGames(listGamesRequest, authDataAccessObject, gameDataAccessObject);
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
    void listGamesErrorInvalidAuthToken() throws BadRequestException {
        helperMethodOne();
        ListGamesRequest listGamesRequest = new ListGamesRequest("1234");
        assertThrows(UnauthorizedException.class, () -> GameService.listGames(listGamesRequest, authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameSuccess() throws BadRequestException {
        helperMethodOne();
        ArrayList<GameData> games = helperMethodTwo();
        assertEquals("First game", games.getFirst().getGameName());
        assertEquals(1, games.getFirst().getGameID());
        assertNull(games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne, authDataAccessObject, gameDataAccessObject);
        assertEquals("Isaac",games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.WHITE, 1);
        GameService.joinGame(joinGameRequestTwo, authDataAccessObject, gameDataAccessObject);
        assertEquals("Isaac",games.getFirst().getBlackUsername());
        assertEquals("Messi",games.getFirst().getWhiteUsername());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorNullParameters() throws BadRequestException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, null, 1);
        assertThrows(BadRequestException.class, () -> GameService.joinGame(joinGameRequestOne, authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameErrorInvalidAuthToken() throws BadRequestException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest("1234", ChessGame.TeamColor.BLACK, 1);
        assertThrows(UnauthorizedException.class, () -> GameService.joinGame(joinGameRequestOne, authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorInvalidGameID() throws BadRequestException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.BLACK, 400);
        assertThrows(BadRequestException.class, () -> GameService.joinGame(joinGameRequestOne, authDataAccessObject, gameDataAccessObject));
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameColorAlreadyTaken() throws BadRequestException {
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne,authDataAccessObject, gameDataAccessObject);
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(authTokenFour, ChessGame.TeamColor.BLACK, 1);
        assertThrows(AlreadyTakenException.class, () -> GameService.joinGame(joinGameRequestTwo, authDataAccessObject, gameDataAccessObject));
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






