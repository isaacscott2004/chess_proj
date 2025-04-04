package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.ListGamesResult;
import ui.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class PostLClient extends Client{
    private final ServerFacade  server;
    private final NotificationHandler notificationHandler;
    private  WebSocketFacade wsFacade;

    public PostLClient(String serverURL, NotificationHandler notificationHandler){
        this.server = new ServerFacade(serverURL);
        this.notificationHandler = notificationHandler;
        this.wsFacade = null;

    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.stripLeading().split(" ");
            String command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command){
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                default -> {
                    Client.calledHelp = true;
                    yield help();
                }
            };

        } catch (ResponseException e){
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
        play <WHITE|BLACK ID>
        observe <ID>
        """;
    }

    @Override
    public String logout()throws ResponseException {
        String authToken = AuthTokenManager.getAuthToken();
        if(authToken == null){
            return "You have not logged in or registered";
        }
        this.server.logout(authToken);

        AuthTokenManager.clearAuthToken();
        return "You have logged out successfully";
    }

    @Override
    public String createGame(String ... params) throws ResponseException {
        if(params.length != 1){
            return "Error: you must enter a gamename. Please make your gamename has no spaces\n" +
                    "Expected: create <gamename>. Examples: gameName, gamename, game_name";
        }
        String authToken = AuthTokenManager.getAuthToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
        this.server.createGame(createGameRequest, authToken);

        return "You have successfully created a new game, game name: " + params[0];
    }
    @Override
    public String listGames()throws ResponseException {;
        ArrayList<GameData> allGames = getListOfGames();
        ArrayList<ClientGameData> listOfGames= new ArrayList<>();
        if(allGames.isEmpty()){
            return "Error: there are no games. Please create a new game by using create <gamename>";
        }
        int count = 1;
        for(GameData gameData : allGames){
            listOfGames.add(new ClientGameData(count, gameData.getGameID(), gameData.getWhiteUsername(),
                    gameData.getBlackUsername(), gameData.getGameName()));
            count++;
        }
        StringBuilder output = new StringBuilder();
        for(ClientGameData clientGameData: listOfGames){
            output.append(clientGameData.toString()).append("\n");
        }
        return output.toString();
    }
    @Override
    public String playGame(String ... params)throws ResponseException {
        ArrayList<GameData> allGames = getListOfGames();
        if(allGames.isEmpty()){
            return "Error: there are no created games please create a new game.\n" +
                    "Expected: create <gamename>";
        }
        if(params.length != 2){
            return "Error: you must enter the color which you want to play as and the gameID.\n" +
                    "Expected: play <WHITE|BLACK ID>";
        }
        if(!(params[0].equalsIgnoreCase("WHITE") | params[0].equalsIgnoreCase("BLACK"))){
            return "Error: make sure you specify which team you want to play as first, then the game id.\n" +
                    "Expected: play <WHITE|BLACK ID>";
        }
        HashMap<Integer, GameData> listNumToGameData = new HashMap<>();
        int count = 1;
        for(GameData gameData: allGames){
            listNumToGameData.put(count, gameData);
            count++;
        }
        String authToken = AuthTokenManager.getAuthToken();
        int choice;
        try{
            choice = Integer.parseInt(params[1]);
        } catch (NumberFormatException e){
            return "Error ID must be a valid number\n" +
                    "Expected play <WHITE|BLACK ID>";
        }
        GameData chosenGame = listNumToGameData.get(choice);
        if(chosenGame == null){
            return "Error: the number you chose is too high.\n" +
                    "Please call list to see which numbers you can choose from\n";
        }
        int gameId = chosenGame.getGameID();
        GameIDManager.setGameID(gameId);
        GameManager.setBoard(chosenGame.getGame().getBoard());

        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[0].toUpperCase());
        JoinGameRequest joinGameRequest = new JoinGameRequest(color, gameId);
        this.server.playGame(joinGameRequest, authToken);

        this.wsFacade = new WebSocketFacade(server.getServerURL(), notificationHandler);
        this.wsFacade.connect(authToken, gameId);

        ChessBoardRep chessBoard = new ChessBoardRep();
        if(color == ChessGame.TeamColor.WHITE){
            return chessBoard.drawBoard(ChessGame.TeamColor.WHITE, GameManager.getBoard(), false, null);
        }else {
            return chessBoard.drawBoard(ChessGame.TeamColor.BLACK, GameManager.getBoard(), false, null);
        }



    }
    @Override
    public String observeGame(String ... params) throws ResponseException {
        ArrayList<GameData> allGames = getListOfGames();
        HashMap<Integer, GameData> listNumToGameData = new HashMap<>();
        int count = 1;
        for(GameData gameData: allGames){
            listNumToGameData.put(count, gameData);
            count++;
        }
        int choice;
        try{
            choice = Integer.parseInt(params[0]);
        } catch (NumberFormatException e){
            return "Error ID must be a valid number\n" +
                    "Expected observe <ID>";
        }
        GameData chosenGame = listNumToGameData.get(choice);
        if(chosenGame == null){
            return """
                    Error: the number you chose is too high.\s
                    Please call list to see which numbers you can choose from
                    """;
        }
        String authToken = AuthTokenManager.getAuthToken();
        this.wsFacade = new WebSocketFacade(server.getServerURL(), notificationHandler);
        this.wsFacade.connect(authToken, choice);
        GameManager.setBoard(chosenGame.getGame().getBoard());
        GameManager.setColor(ChessGame.TeamColor.WHITE);


        ChessBoard board = GameManager.getBoard();
        ChessGame.TeamColor color = GameManager.getColor();
        ChessBoardRep chessBoard = new ChessBoardRep();
        return chessBoard.drawBoard(color, board, false, null) + RESET_TEXT_COLOR + "\nYou are currently viewing " + chosenGame.getGameName() +
                "\nWHITE: " + chosenGame.getWhiteUsername() + ", BLACK: " + chosenGame.getBlackUsername();



    }

    private ArrayList<GameData> getListOfGames() throws ResponseException {
        String authToken = AuthTokenManager.getAuthToken();
        ListGamesResult listGamesResult = this.server.listGames(authToken);
        ArrayList<GameData> allGames = new ArrayList<>(listGamesResult.games());
        allGames.sort(Comparator.comparingInt(GameData::getGameID));
        return  allGames;


    }
}
