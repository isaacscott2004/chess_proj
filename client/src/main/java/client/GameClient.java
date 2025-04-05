package client;

import chess.*;
import client.Managers.*;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class GameClient extends Client{
    private final WebSocketFacade webSocketFacade;
    private final ChessBoardRep chessBoardRep;
    private boolean typedResign;
    private boolean typedMove;

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
                case "resign" -> {
                    if(!ObserverManager.isIsObserver()) {
                        yield askToResign();
                    } else {
                        yield resign();
                    }
                }
                case "highlight" -> highlightMoves(params);
                case "yes" -> {
                    if(typedResign) {
                        this.typedResign = false;
                        yield resign();
                    } else{
                        yield didNotResignMessage();
                    }
                }
                case "q", "r", "b", "n" -> {
                    if(typedMove) {
                        this.typedMove = false;
                        yield promotePiece(command);
                    } else{
                        yield help();
                    }
                }
                case "help" -> {
                    Client.calledHelp = true;
                    yield help();
                }
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
    public String help() {
        return """
        COMMANDS:
        help
        drawboard
        highlight <position>
        move <startPosition, endPosition>
        resign
        leave
        """ + INVISIBLESEPERATOR;
    }

    @Override
    public String leaveGame(){
        if(ObserverManager.isIsObserver()){
            ObserverManager.setIsObserver(false);
        }
        webSocketFacade.leave(AuthTokenManager.getAuthToken(), GameIDManager.getGameID());
        GameIDManager.clearGameID();

        return "";
    }

    @Override
    public String drawBoard(){
        ChessBoard board = GameManager.getBoard();
        ChessGame.TeamColor color = GameManager.getColor();
        return this.chessBoardRep.drawBoard(color, board, false, null, null) + INVISIBLESEPERATOR;
    }

    @Override
    public String movePiece(String ... params){
        if(ObserverManager.isIsObserver()){
            return "Error: observers can't move pieces." + INVISIBLESEPERATOR;
        }

        ChessMove move;
        ChessBoard board = GameManager.getBoard();
        if (params.length !=2){
            return "Error: you must provide a start position and an end position \n" +
                    "Expected: move <startPosition, endPosition> Example: move A3 A4" + INVISIBLESEPERATOR;
        }
        if(params[0].length() != 2 || params[1].length()!= 2){
            return "Error: your positions must contain one letter and one number\n" +
                    "Example A1" + INVISIBLESEPERATOR;
        }
        try{
            move = convertToChessMove(params[0], params[1]);
        } catch (IllegalArgumentException e){
            return (e.getMessage());
        }
        ChessPiece.PieceType type;
        try {
            type = board.getPiece(move.getStartPosition()).getPieceType();
        } catch (NullPointerException e){
            return "Error: there is no piece on that square please choose another piece to move."+ INVISIBLESEPERATOR;
        }
        ChessGame.TeamColor pieceColor = board.getPiece(move.getStartPosition()).getTeamColor();
        if (type == ChessPiece.PieceType.PAWN &&
                (((pieceColor == ChessGame.TeamColor.BLACK && move.getEndPosition().getRow() == 1) ||
                        (pieceColor == ChessGame.TeamColor.WHITE && move.getEndPosition().getRow() == 8)))) {

            MoveManager.setMove(move);
            return askToPromote(move);
        } else {
            webSocketFacade.makeMove(AuthTokenManager.getAuthToken(), GameIDManager.getGameID(), move);
        }
        return "";
    }

    private String promotePiece(String pieceLetter){
        String uppercasePieceLetter = pieceLetter.toUpperCase();
        ChessPiece.PieceType selectedType;
        try {
            selectedType = pieceSelector(uppercasePieceLetter);
        } catch (IllegalArgumentException e){
            return e.getMessage();
        }
        ChessMove preMove = MoveManager.getMove();
        ChessMove promotionMove = new ChessMove(preMove.getStartPosition(),preMove.getEndPosition(),selectedType);
        webSocketFacade.makeMove(AuthTokenManager.getAuthToken(), GameIDManager.getGameID(), promotionMove);
        return "";
    }

    @Override
    public String resign(){
        if(ObserverManager.isIsObserver()){
            return "Error: Observers can't resign!" + INVISIBLESEPERATOR;
        }
        webSocketFacade.resign(AuthTokenManager.getAuthToken(), GameIDManager.getGameID());
        return "";
    }

    @Override
    public String highlightMoves(String ... params){
        ChessPosition position;
        if(params.length != 1){
            return "Error: You must provide a chessPosition. \n" +
                    "The positionYou provide must have a piece on it. \n" +
                    "Expected: highlight<position>, Example: highlight A5" + INVISIBLESEPERATOR;
        }
        if(params[0].length() != 2){
            return "Error: Your chess position must only have a letter for the column and a number for the row. \n" +
                    "Expected: highlight<position>, Example: highlight A5" + INVISIBLESEPERATOR;
        }
        try{
            position = convertToChessPosition(params[0]);
        } catch (IllegalArgumentException e){
            return (e.getMessage());
        }


        ChessBoard board = GameManager.getBoard();
        ChessGame.TeamColor color = GameManager.getColor();
        ChessGame game = GameManager.getGame();
        Collection<ChessMove> moves = game.validMoves(position);
        if(moves == null){
            return "Error: there is no piece at the position you selected" + INVISIBLESEPERATOR;
        }
        ArrayList<ChessPosition> potentialFinalPos = new ArrayList<>();
        for(ChessMove move : moves){
            potentialFinalPos.add(move.getEndPosition());
        }


        return this.chessBoardRep.drawBoard(color, board, true, potentialFinalPos, position) + INVISIBLESEPERATOR;
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
            throw new IllegalArgumentException("Error: The first character must be a-h." + INVISIBLESEPERATOR);
        }
        if(startRow > 8 || startRow < 1 || endRow > 8 || endRow < 1){
            throw new IllegalArgumentException("Error: the second character must be 1-8" + INVISIBLESEPERATOR);
        }

        return new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), null);

    }

    private ChessPosition convertToChessPosition(String position){
        String letters = "abcdefgh";
        char[] positionArray = position.toCharArray();
        int col = letters.indexOf(positionArray[0]) + 1;
        int row = Character.getNumericValue(positionArray[1]);
        if(col == 0){
            throw new IllegalArgumentException("Error: The first character must be a-h." + INVISIBLESEPERATOR);
        }
        if(row > 8  || row < 1){
            throw new IllegalArgumentException("Error: Error: the second character must be 1-8" + INVISIBLESEPERATOR);
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

    private String askToPromote(ChessMove move){
        this.typedMove = true;
        return "Your pawn is moving to row " + move.getEndPosition().getRow() + ".\n" +
                "What piece do you want to promote your pawn to?\n" +
                "Please choose one of the four options Q (queen), R (rook), B (bishop), N (knight)" + INVISIBLESEPERATOR;

    }
    private ChessPiece.PieceType pieceSelector(String pieceLetter){
        ChessPiece.PieceType selectedType;
        switch (pieceLetter){
            case "Q" -> selectedType = ChessPiece.PieceType.QUEEN;
            case "R" -> selectedType = ChessPiece.PieceType.ROOK;
            case "B" -> selectedType = ChessPiece.PieceType.BISHOP;
            case "N" -> selectedType = ChessPiece.PieceType.KNIGHT;
            default -> throw new IllegalArgumentException(
                    "Error: you must choose Q (queen), R (rook), B (bishop), or N (knight)" + INVISIBLESEPERATOR);
        }
        return selectedType;
    }



}