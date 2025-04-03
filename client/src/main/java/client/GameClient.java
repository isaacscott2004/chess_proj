package client;

import chess.ChessMove;
import chess.ChessPosition;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;

public class GameClient extends Client{
    private WebSocketFacade webSocketFacade;
    public GameClient(NotificationHandler notificationHandler, String serverURL){
        this.webSocketFacade = new WebSocketFacade(serverURL, notificationHandler);
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.stripLeading().split(" ");
            String command = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command){
                case "leave" -> leaveGame();
                case "drawboard" -> drawBoard();
                case "move" -> movePiece(params);
                case "resign" -> resign();
                case "highlight" -> highlightMoves(params);
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
        leave
        drawboard
        move <startPosition, endPosition, ?promotionPiece>
        resign
        highlight <position>
        """;
    }

    @Override
    public String leaveGame(){
        webSocketFacade.leave(AuthTokenManager.getAuthToken(), GameIDManager.getGameID());
        GameIDManager.clearGameID();
        return "";
    }

    @Override
    public String drawBoard(){
        return "";
    }

    @Override
    public String movePiece(String ... params){
        ChessMove move;
        if (params.length !=2){
            return "Error: you must provide a start position and an end position \n" +
                    "Expected: move <startPosition, endPosition> Example: move A3 A4";
        }
        if(params[0].length() != 2 || params[1].length()!= 2){
            return "Error: your positions must contain one letter and one number\n" +
                    "Example A1";
        }
        try{
            move = convertString(params[0], params[1]);
        } catch (IllegalArgumentException e){
            return (e.getMessage());
        }
        webSocketFacade.makeMove(AuthTokenManager.getAuthToken(), GameIDManager.getGameID(), move);

        return "";
    }

    @Override
    public String resign(){
        webSocketFacade.resign(AuthTokenManager.getAuthToken(), GameIDManager.getGameID());
        return "";
    }

    @Override
    public String highlightMoves(String ... params){
        return "";
    }

    private ChessMove convertString(String startPosition, String endPosition){
        //assume string length is 2
        String letters = "abcdefgh";
        char[] startPositionArray = startPosition.toCharArray();
        char[] endPositionArray = endPosition.toCharArray();
        int startCol = letters.indexOf(startPositionArray[0]) + 1;
        int endCol = letters.indexOf(endPositionArray[0]) + 1;
        int startRow = Character.getNumericValue(startPositionArray[1]);
        int endRow = Character.getNumericValue(endPositionArray[1]);
        if (startCol == 0 || endCol == 0) {
            throw new IllegalArgumentException("Error: The first character must be a-h.");
        }
        if(startRow > 8 || startRow < 1 || endRow > 8 || endRow < 1){
            throw new IllegalArgumentException("Error: the second character must be 1-8");
        }

        return new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), null);

    }



}
