package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;

import static ui.EscapeSequences.INVISIBLESEPERATOR;
import static ui.EscapeSequences.RESET;

public class GameClient extends Client{
    private final WebSocketFacade webSocketFacade;
    private final ChessBoardRep chessBoardRep;
    private  boolean typedResign;

    public GameClient(String serverURL, NotificationHandler notificationHandler){
        this.webSocketFacade = new WebSocketFacade(serverURL, notificationHandler);
        this.chessBoardRep  = new ChessBoardRep();
        this.typedResign = false;
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
                case "resign" -> askToResign();
                case "highlight" -> highlightMoves(params);
                case "yes" -> {
                    if(typedResign) {
                        this.typedResign = false;
                        yield resign();
                    } else{
                        yield didNotResignMessage();
                    }
                }
                case "no" -> didNotResignMessage();
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
        """ + INVISIBLESEPERATOR;
    }

    @Override
    public String leaveGame(){
        webSocketFacade.leave(AuthTokenManager.getAuthToken(), GameIDManager.getGameID());
        GameIDManager.clearGameID();
        return "";
    }

    @Override
    public String drawBoard(){
        ChessBoard board = GameManager.getBoard();
        ChessGame.TeamColor color = GameManager.getColor();
        return this.chessBoardRep.drawBoard(color, board, false, null) + INVISIBLESEPERATOR;
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
            move = convertToChessMove(params[0], params[1]);
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
        ChessPosition position;
        if(params.length != 1){
            return "Error: You must provide a chessPosition. \n" +
                    "The positionYou provide must have a piece on it. \n" +
                    "Expected: highlight<position>, Example: highlight A5";
        }
        if(params[0].length() != 2){
            return "Error: Your chess position much only have a letter for the column and a number for the row. \n" +
                    "Expected: highlight<position>, Example: highlight A5";
        }
        try{
            position = convertToChessPosition(params[0]);
        } catch (IllegalArgumentException e){
            return (e.getMessage());
        }

        ChessBoard board = GameManager.getBoard();
        ChessGame.TeamColor color = GameManager.getColor();

        return this.chessBoardRep.drawBoard(color, board, true, position) + INVISIBLESEPERATOR;
    }





    private ChessMove convertToChessMove(String startPosition, String endPosition){
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

    private ChessPosition convertToChessPosition(String position){
        String letters = "abcdefgh";
        char[] positionArray = position.toCharArray();
        int col = letters.indexOf(positionArray[0]) + 1;
        int row = Character.getNumericValue(positionArray[1]);
        if(col == 0){
            throw new IllegalArgumentException("Error: The first character must be a-h.");
        }
        if(row > 8  || row < 1){
            throw new IllegalArgumentException("Error: Error: the second character must be 1-8");
        }
        return new ChessPosition(row, col);
    }

    private String askToResign(){
        this.typedResign = true;
        return "Are you sure you want to resign? \nType 'yes' to resign or 'no' to keep playing." + INVISIBLESEPERATOR;
    }
    private String didNotResignMessage(){
        return "You did not resign." + INVISIBLESEPERATOR;
    }

}