package client;
import dataaccess.DataAccessException;
import request.LoginRequest;
import request.RegisterRequest;
import ui.ServerFacade;

import java.util.Arrays;

public class PreLClient extends Client {
    private final ServerFacade server;

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
            return "You must enter your username and password. Expected: login <username password>";
        }
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            this.server.login(loginRequest);
            return params[0] + ", you have logged in successfully";
    }
    @Override
    public String register(String... params) throws DataAccessException {
        if(params.length != 3){
            return "You must enter your username, password, and email. Expected: register <username password email>";
        }

            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            this.server.register(registerRequest);
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
//    public String eval(String input) {
//        try {
//            var tokens = input.toLowerCase().split(" ");
//            var cmd = (tokens.length > 0) ? tokens[0] : "help";
//            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
//            return switch (cmd) {
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
//                case "quit" -> "quit";
//                default -> help();
//            };
//        } catch (ResponseException ex) {
//            return ex.getMessage();
//        }
//    }

}
