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
import result.JoinGameResult;
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
    public static void setUp(){
        userDataAccessObject = DAOImplmentation.getUserDAO();
        authDataAccessObject = DAOImplmentation.getAuthDAO();
        gameDataAccessObject = DAOImplmentation.getGameDAO();
        userDataList = userDataAccessObject.getUserDataStorage();
        authDataList = authDataAccessObject.getAuthDataStorage();
        RegisterRequest registerRequestOne = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResultOne = UserService.register(registerRequestOne);
        authTokenOne = registerResultOne.authToken();

        RegisterRequest registerRequestTwo = new RegisterRequest("Messi", "Barcelona", "messi@gmail.com");
        RegisterResult registerResultTwo = UserService.register(registerRequestTwo);
        authTokenTwo = registerResultTwo.authToken();

        RegisterRequest registerRequestThree = new RegisterRequest("Ronaldo", "Madrid", "cr7@gmail.com");
        RegisterResult registerResultThree = UserService.register(registerRequestThree);
        authTokenThree = registerResultThree.authToken();

        RegisterRequest registerRequestFour = new RegisterRequest("Neymar", "Paris", "brazil@gmail.com");
        RegisterResult registerResultFour = UserService.register(registerRequestFour);
        authTokenFour = registerResultFour.authToken();


    }
    // helper methods
    void helperMethodOne() {
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne);
        GameService.createGame(createGameRequestOne);

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Second game", authTokenTwo);
        GameService.createGame(createGameRequestTwo);

        CreateGameRequest createGameRequestThree = new CreateGameRequest("Third game", authTokenThree);
        GameService.createGame(createGameRequestThree);

        CreateGameRequest createGameRequestFour = new CreateGameRequest("Fourth game", authTokenFour);
        GameService.createGame(createGameRequestFour);

    }
    ArrayList<GameData> helperMethodTwo(){
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenOne);
        ListGamesResult listGamesResult = GameService.listGames(listGamesRequest);
        return (ArrayList<GameData>)listGamesResult.games();
    }

    @Test
    void preTest(){
        assertEquals(4, userDataList.size());
        assertEquals(4, authDataList.size());
    }

    @Test
    void createGameSuccess(){
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne);
        CreateGameResult createGameResultOne = GameService.createGame(createGameRequestOne);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultOne.gameID());
        assertNull(createGameResultOne.message());

        CreateGameRequest createGameRequestTwo = new CreateGameRequest("second game", authTokenTwo);
        CreateGameResult createGameResultTwo = GameService.createGame(createGameRequestTwo);
        assertEquals(2, gameDataAccessObject.getListGames().size());
        assertEquals(2, createGameResultTwo.gameID());
        assertNull(createGameResultTwo.message());
        gameDataAccessObject.clearGameData();

        CreateGameRequest createGameRequestThree = new CreateGameRequest("First game after clear", authTokenThree);
        CreateGameResult createGameResultThree = GameService.createGame(createGameRequestThree);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        assertEquals(1, createGameResultThree.gameID());
        assertNull(createGameResultThree.message());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorNullParameters() {
        CreateGameRequest createGameRequestOne = new CreateGameRequest(null, authTokenOne);
        CreateGameResult createGameResultOne = GameService.createGame(createGameRequestOne);
        assertNull(createGameResultOne.gameID());
        assertEquals("Error: (gameName and/or authToken must not be null)", createGameResultOne.message());
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorInvalidAuthToken(){
        CreateGameRequest createGameRequestOne = new CreateGameRequest("test game", "1234");
        CreateGameResult createGameResultOne = GameService.createGame(createGameRequestOne);
        assertNull(createGameResultOne.gameID());
        assertEquals("Error: unauthorized", createGameResultOne.message());
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void createGameErrorGameNameTaken(){
        CreateGameRequest createGameRequestOne = new CreateGameRequest("First game", authTokenOne);
        GameService.createGame(createGameRequestOne);
        CreateGameRequest createGameRequestTwo = new CreateGameRequest("First game", authTokenTwo);
        assertEquals(1, gameDataAccessObject.getListGames().size());
        CreateGameResult createGameResultTwo = GameService.createGame(createGameRequestTwo);
        assertNull(createGameResultTwo.gameID());
        assertEquals("Error: (gameName already taken)", createGameResultTwo.message());
        assertEquals(1, gameDataAccessObject.getListGames().size());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());


    }

    @Test
    void listGamesSuccess(){
        helperMethodOne();
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenOne);
        ListGamesResult listGamesResult = GameService.listGames(listGamesRequest);
        Collection<GameData> games = listGamesResult.games();
        ArrayList<GameData> gamesArray = (ArrayList<GameData>)games;
        assertEquals(4, games.size());
        assertNull(listGamesResult.message());
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
    void listGamesErrorInvalidAuthToken(){
        helperMethodOne();
        ListGamesRequest listGamesRequest = new ListGamesRequest("1234");
        ListGamesResult listGamesResult = GameService.listGames(listGamesRequest);
        assertEquals("Error: unauthorized", listGamesResult.message());
        assertNull(listGamesResult.games());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameSuccess(){
        helperMethodOne();
        ArrayList<GameData> games = helperMethodTwo();
        assertEquals("First game", games.getFirst().getGameName());
        assertEquals(1, games.getFirst().getGameID());
        assertNull(games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, ChessGame.TeamColor.BLACK, 1);
        JoinGameResult joinGameResultOne = GameService.joinGame(joinGameRequestOne);
        assertNull(joinGameResultOne.message());
        assertEquals("Isaac",games.getFirst().getBlackUsername());
        assertNull(games.getFirst().getWhiteUsername());
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.WHITE, 1);
        JoinGameResult joinGameResultTwo = GameService.joinGame(joinGameRequestTwo);
        assertNull(joinGameResultTwo.message());
        assertEquals("Isaac",games.getFirst().getBlackUsername());
        assertEquals("Messi",games.getFirst().getWhiteUsername());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorNullParameters(){
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, null, 1);
        JoinGameResult joinGameResultOne = GameService.joinGame(joinGameRequestOne);
        assertEquals( "Error: (authToken and/or color and/or gameID cannot be empty)",joinGameResultOne.message());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameErrorInvalidAuthToken(){
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest("1234", ChessGame.TeamColor.BLACK, 1);
        JoinGameResult joinGameResultOne = GameService.joinGame(joinGameRequestOne);
        assertEquals( "Error: unauthorized",joinGameResultOne.message());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());

    }

    @Test
    void joinGameErrorInvalidGameID(){
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenTwo, ChessGame.TeamColor.BLACK, 400);
        JoinGameResult joinGameResultOne = GameService.joinGame(joinGameRequestOne);
        assertEquals( "Error: (Invalid gameID)",joinGameResultOne.message());
        gameDataAccessObject.clearGameData();
        assertTrue(gameDataAccessObject.getListGames().isEmpty());
    }

    @Test
    void joinGameColorAlreadyTaken(){
        helperMethodOne();
        JoinGameRequest joinGameRequestOne = new JoinGameRequest(authTokenOne, ChessGame.TeamColor.BLACK, 1);
        GameService.joinGame(joinGameRequestOne);
        JoinGameRequest joinGameRequestTwo = new JoinGameRequest(authTokenFour, ChessGame.TeamColor.BLACK, 1);
        JoinGameResult joinGameResultTwo = GameService.joinGame(joinGameRequestTwo);
        assertEquals("Error: already taken", joinGameResultTwo.message());
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






