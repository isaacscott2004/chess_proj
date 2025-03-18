package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.ListGamesResult;
import ui.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class PostLClient extends Client{
//    private static HashMap<Integer, Integer> numberToGameId = new HashMap<>();
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
                case "play" -> playGame(params);
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
        play <WHITE|BLACK ID>
        observe <ID>
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
                    "Expected: create <gamename>. Examples: gameName, gamename, game_name";
        }
        String authToken = AuthTokenManager.getAuthToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
        this.server.createGame(createGameRequest, authToken);

        return "You have successfully created a new game, game name: " + params[0];
    }
    @Override
    public String listGames()throws DataAccessException {;
        ArrayList<GameData> allGames = getListOfGames();
        ArrayList<ClientGameData> listOfGames= new ArrayList<>();
        if(allGames.isEmpty()){
            return "Error: there are no games. Please create a new game by using create <gamename>";
        }
        int count = 1;
        for(GameData gameData : allGames){
            listOfGames.add(new ClientGameData(count, gameData.getGameID(), gameData.getWhiteUsername(), gameData.getBlackUsername(), gameData.getGameName()));
            count++;
        }
        StringBuilder output = new StringBuilder();
        for(ClientGameData clientGameData: listOfGames){
            output.append(clientGameData.toString()).append("\n");
        }
        return output.toString();
    }
    @Override
    public String playGame(String ... params)throws DataAccessException {
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
        allGames.sort(Comparator.comparingInt(GameData::getGameID));
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
        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[0].toUpperCase());
        JoinGameRequest joinGameRequest = new JoinGameRequest(color, gameId);
        this.server.playGame(joinGameRequest, authToken);
        if(color == ChessGame.TeamColor.WHITE){
            return drawBoardWhite();
        }
        return drawBoardBlack();



    }
    private ArrayList<GameData> getListOfGames() throws DataAccessException {
        String authToken = AuthTokenManager.getAuthToken();
        ListGamesResult listGamesResult = this.server.listGames(authToken);
        return new ArrayList<>(listGamesResult.games());

    }

    private String drawBoardWhite(){
      return SET_BG_COLOR_BLUE+SET_TEXT_COLOR_BLACK+ "   "+" a "+" b "+" c "+" d "+" e "+" f "+" g "+" h "+"   "+RESET_BG_COLOR+EMPTY+"\n"
            + SET_BG_COLOR_BLUE + " 8 " + SET_BG_COLOR_LIGHT_TAN + BLACK_ROOK + SET_BG_COLOR_DARK_TAN + BLACK_KNIGHT +
              SET_BG_COLOR_LIGHT_TAN + BLACK_BISHOP + SET_BG_COLOR_DARK_TAN + BLACK_QUEEN + SET_BG_COLOR_LIGHT_TAN +
              BLACK_KING + SET_BG_COLOR_DARK_TAN + BLACK_BISHOP + SET_BG_COLOR_LIGHT_TAN + BLACK_KNIGHT +
              SET_BG_COLOR_DARK_TAN + BLACK_ROOK + SET_BG_COLOR_BLUE + " 8 " + RESET_BG_COLOR + EMPTY + "\n"
              + SET_BG_COLOR_BLUE + " 7 " + SET_BG_COLOR_DARK_TAN + BLACK_PAWN +SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN +
              SET_BG_COLOR_DARK_TAN + BLACK_PAWN + SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN + SET_BG_COLOR_DARK_TAN +
              BLACK_PAWN + SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN + SET_BG_COLOR_DARK_TAN + BLACK_PAWN +
              SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN + SET_BG_COLOR_BLUE + " 7 " + RESET_BG_COLOR + EMPTY + "\n"
              + SET_BG_COLOR_BLUE + " 6 " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
              SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
              SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
              SET_BG_COLOR_BLUE + " 6 " + RESET_BG_COLOR + EMPTY + "\n"
              + SET_BG_COLOR_BLUE + " 5 " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
              SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
              SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
              SET_BG_COLOR_BLUE + " 5 " + RESET_BG_COLOR + EMPTY + "\n"
              + SET_BG_COLOR_BLUE + " 4 " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
              SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
              SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
              SET_BG_COLOR_BLUE + " 4 " + RESET_BG_COLOR + EMPTY + "\n"
              + SET_BG_COLOR_BLUE + " 3 " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
              SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
              SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
              SET_BG_COLOR_BLUE + " 3 " + RESET_BG_COLOR + EMPTY + "\n"
              + SET_BG_COLOR_BLUE + " 2 " + SET_BG_COLOR_LIGHT_TAN + WHITE_PAWN +SET_BG_COLOR_DARK_TAN + WHITE_PAWN +
              SET_BG_COLOR_LIGHT_TAN + WHITE_PAWN + SET_BG_COLOR_DARK_TAN + WHITE_PAWN + SET_BG_COLOR_LIGHT_TAN +
              WHITE_PAWN + SET_BG_COLOR_DARK_TAN + WHITE_PAWN + SET_BG_COLOR_LIGHT_TAN + WHITE_PAWN +
              SET_BG_COLOR_DARK_TAN + WHITE_PAWN + SET_BG_COLOR_BLUE + " 2 " + RESET_BG_COLOR + EMPTY + "\n"
              + SET_BG_COLOR_BLUE + " 1 " + SET_BG_COLOR_DARK_TAN + WHITE_ROOK + SET_BG_COLOR_LIGHT_TAN + WHITE_KNIGHT +
              SET_BG_COLOR_DARK_TAN + WHITE_BISHOP + SET_BG_COLOR_LIGHT_TAN + WHITE_QUEEN + SET_BG_COLOR_DARK_TAN +
              WHITE_KING + SET_BG_COLOR_LIGHT_TAN + WHITE_BISHOP + SET_BG_COLOR_DARK_TAN + WHITE_KNIGHT +
              SET_BG_COLOR_LIGHT_TAN + WHITE_ROOK + SET_BG_COLOR_BLUE + " 1 " + RESET_BG_COLOR + EMPTY + "\n" +
              SET_BG_COLOR_BLUE+SET_TEXT_COLOR_BLACK+ "   "+" a "+" b "+" c "+" d "+" e "+" f "+" g "+" h "+"   "+RESET_BG_COLOR+EMPTY;

    }
    private String drawBoardBlack(){
        return SET_BG_COLOR_BLUE+SET_TEXT_COLOR_BLACK+ "   "+" a "+" b "+" c "+" d "+" e "+" f "+" g "+" h "+"   "+RESET_BG_COLOR+EMPTY+"\n"
                + SET_BG_COLOR_BLUE + " 1 " + SET_BG_COLOR_DARK_TAN + WHITE_ROOK + SET_BG_COLOR_LIGHT_TAN + WHITE_KNIGHT +
                SET_BG_COLOR_DARK_TAN + WHITE_BISHOP + SET_BG_COLOR_LIGHT_TAN + WHITE_QUEEN + SET_BG_COLOR_DARK_TAN +
                WHITE_KING + SET_BG_COLOR_LIGHT_TAN + WHITE_BISHOP + SET_BG_COLOR_DARK_TAN + WHITE_KNIGHT +
                SET_BG_COLOR_LIGHT_TAN + WHITE_ROOK + SET_BG_COLOR_BLUE + " 1 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE + " 2 " + SET_BG_COLOR_LIGHT_TAN + WHITE_PAWN + SET_BG_COLOR_DARK_TAN + WHITE_PAWN +
                SET_BG_COLOR_LIGHT_TAN + WHITE_PAWN + SET_BG_COLOR_DARK_TAN + WHITE_PAWN + SET_BG_COLOR_LIGHT_TAN +
                WHITE_PAWN + SET_BG_COLOR_DARK_TAN + WHITE_PAWN + SET_BG_COLOR_LIGHT_TAN + WHITE_PAWN +
                SET_BG_COLOR_DARK_TAN + WHITE_PAWN + SET_BG_COLOR_BLUE + " 2 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE + " 3 " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
                SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
                SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
                SET_BG_COLOR_BLUE + " 3 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE + " 4 " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
                SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
                SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
                SET_BG_COLOR_BLUE + " 4 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE + " 5 " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
                SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
                SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
                SET_BG_COLOR_BLUE + " 5 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE + " 6 " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
                SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " +
                SET_BG_COLOR_DARK_TAN + "   " + SET_BG_COLOR_LIGHT_TAN + "   " + SET_BG_COLOR_DARK_TAN + "   " +
                SET_BG_COLOR_BLUE + " 6 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE + " 7 " + SET_BG_COLOR_DARK_TAN + BLACK_PAWN + SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN +
                SET_BG_COLOR_DARK_TAN + BLACK_PAWN + SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN + SET_BG_COLOR_DARK_TAN +
                BLACK_PAWN + SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN + SET_BG_COLOR_DARK_TAN + BLACK_PAWN +
                SET_BG_COLOR_LIGHT_TAN + BLACK_PAWN + SET_BG_COLOR_BLUE + " 7 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE + " 8 " + SET_BG_COLOR_LIGHT_TAN + BLACK_ROOK + SET_BG_COLOR_DARK_TAN + BLACK_KNIGHT +
                SET_BG_COLOR_LIGHT_TAN + BLACK_BISHOP + SET_BG_COLOR_DARK_TAN + BLACK_QUEEN + SET_BG_COLOR_LIGHT_TAN +
                BLACK_KING + SET_BG_COLOR_DARK_TAN + BLACK_BISHOP + SET_BG_COLOR_LIGHT_TAN + BLACK_KNIGHT +
                SET_BG_COLOR_DARK_TAN + BLACK_ROOK + SET_BG_COLOR_BLUE + " 8 " + RESET_BG_COLOR + EMPTY + "\n"
                + SET_BG_COLOR_BLUE +SET_TEXT_COLOR_BLACK+ "   "+" a "+" b "+" c "+" d "+" e "+" f "+" g "+" h "+"   "+RESET_BG_COLOR+EMPTY;
    }



}
