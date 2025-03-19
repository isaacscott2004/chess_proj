package client;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.ListGamesResult;
import result.RegisterResult;
import server.Server;
import service.ClearService;
import ui.ServerFacade;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static GameDAO gameDataAccessObject;
    private static UserDAO userDataAccessObject;
    private static AuthDAO authDataAccessObject;
    private static ServerFacade testServerFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        gameDataAccessObject = new MySqlGameDAO();
        userDataAccessObject = new MySqlUserDAO();
        authDataAccessObject = new MySqlAuthDAO();
        testServerFacade = new ServerFacade("http://localhost:" + port);


    }
    @BeforeEach
    public void setup() throws DataAccessException {
        ClearService.clear(authDataAccessObject, userDataAccessObject, gameDataAccessObject);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException, DataAccessException {
        RegisterRequest request = new RegisterRequest("Isaac", "1234", "isi@hotmail.com");
        testServerFacade.register(request);
        ArrayList<UserData> listOfUsers = new ArrayList<>(userDataAccessObject.getUserDataStorage());
        ArrayList<AuthData> listOfAuths = new ArrayList<>(authDataAccessObject.getAuthDataStorage());
        assertEquals(1, listOfUsers.size());
        assertEquals(1, listOfAuths.size());
        assertEquals("Isaac", listOfUsers.getFirst().getUsername());
        assertEquals("isi@hotmail.com", listOfUsers.getFirst().getEmail());



    }
    @Test
    public void registerThrowsException() throws ResponseException {
        RegisterRequest requestOne = new RegisterRequest("Isaac", "1234", "isi@hotmail.com");
        testServerFacade.register(requestOne);
        RegisterRequest requestTwo = new RegisterRequest("Isaac", "1234", "isi@hotmail.com");
        assertThrows(ResponseException.class, () -> testServerFacade.register(requestTwo));
    }
    @Test
    public void logoutSuccess() throws ResponseException, DataAccessException {
        RegisterRequest request = new RegisterRequest("Isaac", "1234", "isi@hotmail.com");
        RegisterResult registerResult = testServerFacade.register(request);
        ArrayList<UserData> listOfUsers = new ArrayList<>(userDataAccessObject.getUserDataStorage());
        ArrayList<AuthData> listOfAuths = new ArrayList<>(authDataAccessObject.getAuthDataStorage());
        assertEquals(1, listOfUsers.size());
        assertEquals(1, listOfAuths.size());
        testServerFacade.logout(registerResult.authToken());
        listOfUsers = new ArrayList<>(userDataAccessObject.getUserDataStorage());
        listOfAuths = new ArrayList<>(authDataAccessObject.getAuthDataStorage());
        assertEquals(1, listOfUsers.size());
        assertTrue(listOfAuths.isEmpty());
    }

    @Test
    public void logoutThrowsExceptionUnauthorized() throws ResponseException {
        String fakeAuth = "1234567";
        assertThrows(ResponseException.class, () -> testServerFacade.logout(fakeAuth));
    }

    @Test
    public void loginSuccess() throws ResponseException, DataAccessException {
        RegisterRequest request = new RegisterRequest("Isaac", "1234", "isi@hotmail.com");
        RegisterResult registerResult = testServerFacade.register(request);
        ArrayList<UserData> listOfUsers = new ArrayList<>(userDataAccessObject.getUserDataStorage());
        ArrayList<AuthData> listOfAuths = new ArrayList<>(authDataAccessObject.getAuthDataStorage());
        assertEquals(1, listOfUsers.size());
        assertEquals(1, listOfAuths.size());
        testServerFacade.logout(registerResult.authToken());
        listOfUsers = new ArrayList<>(userDataAccessObject.getUserDataStorage());
        listOfAuths = new ArrayList<>(authDataAccessObject.getAuthDataStorage());
        assertEquals(1, listOfUsers.size());
        assertTrue(listOfAuths.isEmpty());
        LoginRequest loginRequest = new LoginRequest("Isaac", "1234");
        testServerFacade.login(loginRequest);
        listOfUsers = new ArrayList<>(userDataAccessObject.getUserDataStorage());
        listOfAuths = new ArrayList<>(authDataAccessObject.getAuthDataStorage());
        assertEquals(1, listOfUsers.size());
        assertEquals(1, listOfAuths.size());
    }

    @Test
    public void loginWithoutRegistering(){
        assertThrows(ResponseException.class, () -> testServerFacade.login(new LoginRequest("Isaac", "1234")));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        ArrayList<String> authTokens = preGameMethod();
        CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
        testServerFacade.createGame(createGameRequest, authTokens.getFirst());
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDataAccessObject.getListGames());
        assertEquals(1, listOfGames.size());
    }

    @Test
    public void createGameFailureUnauthorized() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
        assertThrows(ResponseException.class, () -> testServerFacade.createGame(createGameRequest, "123456"));


    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        ArrayList<String> authTokens = preGameMethod();
        CreateGameRequest createGameRequestOne = new CreateGameRequest("testGameOne");
        CreateGameRequest createGameRequestTwo = new CreateGameRequest("testGameTwo");
        testServerFacade.createGame(createGameRequestOne, authTokens.getFirst());
        testServerFacade.createGame(createGameRequestTwo, authTokens.getFirst());
        ListGamesResult listGamesResult = testServerFacade.listGames(authTokens.getFirst());
        ArrayList<GameData> arrayGames = new ArrayList<>(listGamesResult.games());
        assertEquals(2, arrayGames.size());
        ArrayList<String> expectedNames = new ArrayList<>();
        expectedNames.add("testGameOne");
        expectedNames.add("testGameTwo");
        int i = 0;
        for(GameData game: arrayGames){
            assertEquals(expectedNames.get(i), game.getGameName());
            assertEquals(i + 1, game.getGameID());
            i++;
        }
    }

    @Test
    public void listGamesUnauthorized() throws ResponseException {
        ArrayList<String> authTokens = preGameMethod();
        CreateGameRequest createGameRequestOne = new CreateGameRequest("testGameOne");
        CreateGameRequest createGameRequestTwo = new CreateGameRequest("testGameTwo");
        testServerFacade.createGame(createGameRequestOne, authTokens.getFirst());
        testServerFacade.createGame(createGameRequestTwo, authTokens.getFirst());
        assertThrows(ResponseException.class, () -> testServerFacade.listGames("123456"));
    }

    @Test
    public void playGameSuccess() throws ResponseException, DataAccessException {
        ArrayList<String> authTokens = preGameMethod();
        CreateGameRequest createGameRequestOne = new CreateGameRequest("testGameOne");
        testServerFacade.createGame(createGameRequestOne, authTokens.getFirst());
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        testServerFacade.playGame(joinGameRequest, authTokens.getFirst());
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDataAccessObject.getListGames());
        GameData firstGame = listOfGames.getFirst();
        assertEquals("Isaac", firstGame.getWhiteUsername());
    }

    @Test
    public void playGameAttemptJoinSameTeam() throws ResponseException, DataAccessException {
        ArrayList<String> authTokens = preGameMethod();
        CreateGameRequest createGameRequestOne = new CreateGameRequest("testGameOne");
        testServerFacade.createGame(createGameRequestOne, authTokens.getFirst());
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);
        testServerFacade.playGame(joinGameRequest, authTokens.getFirst());
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDataAccessObject.getListGames());
        GameData firstGame = listOfGames.getFirst();
        assertEquals("Isaac", firstGame.getWhiteUsername());
        assertThrows(ResponseException.class, () -> testServerFacade.playGame(joinGameRequest, authTokens.getFirst()));

    }


    private ArrayList<String> preGameMethod() throws ResponseException {
        ArrayList<String> authTokens = new ArrayList<>();
        RegisterRequest requestOne = new RegisterRequest("Isaac", "1234", "isi@hotmail.com");
        RegisterRequest requestTwo = new RegisterRequest("Bob", "1234", "bob@hotmail.com");
        RegisterResult registerResultOne = testServerFacade.register(requestOne);
        RegisterResult registerResultTwo = testServerFacade.register(requestTwo);
        authTokens.add(registerResultOne.authToken());
        authTokens.add(registerResultTwo.authToken());
        return authTokens;


    }




}
