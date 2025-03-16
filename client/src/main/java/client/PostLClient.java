package client;

import dataaccess.DataAccessException;
import result.LogoutResult;
import ui.Repl;
import ui.ServerFacade;

import java.util.Arrays;

public class PostLClient extends Client{
    private final ServerFacade  server;
    private static final String SERVERURL = "http://localhost:8080";
    public PostLClient(String serverURL){
        this.server = new ServerFacade(serverURL);
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command.toLowerCase()){
                case "logout" -> logout();
                case "create game" -> createGame(params);
                case "list games" -> listGames();
                case "play game" -> playGame();
                case "observe game" -> observeGame();
                default -> help();
            };

        } catch (DataAccessException e){
            return e.getMessage();
        }

    }

    @Override
    public String help() {
        return """
        logout
        create game <gameName>
        list games
        play game <username>
        observe game <username>
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
        new Repl(SERVERURL, ClientType.PREL).run();
        return "You have logged out successfully";
    }

}
