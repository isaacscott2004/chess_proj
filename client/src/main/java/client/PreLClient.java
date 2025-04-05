package client;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import ui.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PreLClient extends Client {
    private final ServerFacade server;
    public PreLClient(String serverURL){
        this.server = new ServerFacade(serverURL);
    }
    @Override
    public String eval(String input){
        try {
            String[] tokens = input.stripLeading().split(" ");
            String command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command){
                case "quit" -> "You have quit the program, bye.";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> {
                    Client.calledHelp = true;
                    yield SET_TEXT_COLOR_RED + "UNKNOWN COMMAND: " + RESET_TEXT_COLOR + "\n" + help();
                }
            };
        } catch (ResponseException e){
            return e.getMessage();
        }
    }
    @Override
    public String login(String... params) throws ResponseException {
        if(params.length != 2){
            return "Error: you must enter a username and password. Expected: login <username password>";
        }
        LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
        LoginResult loginResult = this.server.login(loginRequest);
        AuthTokenManager.setAuthToken(loginResult.authToken());
        return params[0] + ", you have logged in successfully";

    }
    @Override
    public String register(String... params) throws ResponseException {
        if(params.length != 3){
            return "Error: you must enter a username, password, and email. Expected: register <username password email>";
        }

        RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
        RegisterResult registerResult = this.server.register(registerRequest);
        AuthTokenManager.setAuthToken(registerResult.authToken());
        return params[0] + ", you have registered successfully";
    }
    @Override
    public String help(){
        return """
        help
        register <username password email>
        login <username password>
        quit
        
        """;

    }



}
