package client;

import dataaccess.DataAccessException;
import request.CreateGameRequest;
import result.CreateGameResult;
import ui.ServerFacade;

import java.util.Arrays;

public class PostLClient extends Client{
    private final ServerFacade  server;
    public PostLClient(String serverURL){
        this.server = new ServerFacade(serverURL);
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command){
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame();
                case "observe" -> observeGame();
                default -> {
                    Client.calledHelp = true;
                    yield help();
                }
            };

        } catch (DataAccessException e){
            return e.getMessage();
        }

    }

    @Override
    public String help() {
        return """
        help
        logout
        create <gamename>
        list
        play <username>
        observe <username>
        """;
    }

    @Override
    public String logout()throws DataAccessException {
        String authToken = AuthTokenManager.getAuthToken();
        if(authToken == null){
            return "You have not logged in or registered";
        }
        this.server.logout(authToken);

        AuthTokenManager.clearAuthToken();
        return "You have logged out successfully";
    }

    @Override
    public String createGame(String ... params) throws DataAccessException {
        if(params.length != 1){
            return "Error: you must enter a gamename. Please make your gamename has no spaces\n" +
                    "Expected: create <gameName>. Examples: gameName, gamename, game_name";
        }
        String authToken = AuthTokenManager.getAuthToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
        CreateGameResult createGameResult = this.server.createGame(createGameRequest, authToken);
        return "You have successfully created a new game, game name: " + params[0] + " ,id " + createGameResult.gameID();
    }

}
