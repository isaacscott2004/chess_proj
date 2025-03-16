package client;
import dataaccess.DataAccessException;
import request.LoginRequest;
import request.RegisterRequest;
import ui.Repl;
import ui.ServerFacade;

import java.util.Arrays;

public class PreLClient extends Client {
    private final ServerFacade server;
    private static final String SERVERURL = "http://localhost:8080";

    public PreLClient(String serverURL){
        this.server = new ServerFacade(serverURL);
    }
    @Override
    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command.toLowerCase()){
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (DataAccessException e){
            return e.getMessage();
        }
    }
    @Override
    public String login(String... params) throws DataAccessException {
        if(params.length != 2){
            return "You must enter a username and password. Expected: login <username password>";
        }
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            this.server.login(loginRequest);
            new Repl(SERVERURL, ClientType.POSTL).run();
            return params[0] + ", you have logged in successfully";

    }
    @Override
    public String register(String... params) throws DataAccessException {
        if(params.length != 3){
            return "You must enter a username, password, and email. Expected: register <username password email>";
        }

            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            this.server.register(registerRequest);
            new Repl(SERVERURL, ClientType.POSTL).run();
            return params[0] + ", you have registered successfully";



    }
    @Override
    public String help(){
        return """
        register <username password email>
        login <username password>
        quit
        """;

    }


}
