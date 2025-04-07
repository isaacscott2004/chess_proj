package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.EMPTY;

public class ChessBoardRep {

    public String drawBoard(ChessGame.TeamColor color, ChessBoard board,
                            boolean highlightBoard, ArrayList<ChessPosition> potentialFinalPositions, ChessPosition startPosition){
        ChessPiece[][] allPieces = board.getBoard();
        int count;
        StringBuilder restOfBoard = new StringBuilder();
        if (color == ChessGame.TeamColor.BLACK){
            count = 1;
            restOfBoard.append(drawFirstAndLastRow(true)).append("\n");
            drawBlackRows(restOfBoard, allPieces, count, highlightBoard, board, startPosition, potentialFinalPositions);
            restOfBoard.append(drawFirstAndLastRow(true));
        }
        else {
            count = 8;
            restOfBoard.append(drawFirstAndLastRow(false)).append("\n");
            drawWhiteRows(restOfBoard, allPieces, count, highlightBoard, board, startPosition, potentialFinalPositions);
            restOfBoard.append(drawFirstAndLastRow(false));
        }
        return restOfBoard.toString();
    }

    private void drawBlackRows(StringBuilder restOfBoard, ChessPiece[][] allPieces, int count,
                               boolean highlightBoard, ChessBoard board,
                               ChessPosition startPosition, ArrayList<ChessPosition> potentialFinalPositions){
        for (int i = 0; i < allPieces.length; i++) {
            drawRowLabel(restOfBoard, count);
            boolean isLight = count % 2 != 0;
            for (int j = 7; j >= 0; j--) {
                drawSquare(restOfBoard, isLight, highlightBoard, i, j, startPosition, potentialFinalPositions);
                drawPieces(board, i, j, restOfBoard);
                isLight = !isLight;

            }
            drawRowLabel(restOfBoard, count);
            restOfBoard.append(RESET_BG_COLOR).append(EMPTY).append("\n");
            count++;

        }
    }

    private void drawWhiteRows(StringBuilder restOfBoard, ChessPiece[][] allPieces, int count,
                               boolean highlightBoard, ChessBoard board,
                               ChessPosition startPosition, ArrayList<ChessPosition> potentialFinalPositions) {
        for (int i = 7; i >= 0; i--) {
            drawRowLabel(restOfBoard, count);
            boolean isLight = count % 2 == 0;
            for (int j = 0; j < allPieces[0].length; j++) {
                drawSquare(restOfBoard, isLight, highlightBoard, i, j, startPosition, potentialFinalPositions);
                drawPieces(board, i, j, restOfBoard);
                isLight = !isLight;
            }
            drawRowLabel(restOfBoard, count);
            restOfBoard.append(RESET_BG_COLOR).append(EMPTY).append("\n");
            count--;

        }
    }

    private void drawSquare(StringBuilder restOfBoard, boolean isLight, boolean highlightBoard, int i, int j,
                            ChessPosition startPosition, ArrayList<ChessPosition> potentialFinalPositions){
        if(highlightBoard){
            ChessPosition cPosition = new ChessPosition(i + 1, j + 1);
            boolean startPos = cPosition.equals(startPosition);
            boolean highlight = potentialFinalPositions.contains(cPosition);
            if(startPos) {
                restOfBoard.append(SET_BG_COLOR_YELLOW);
            }else if(highlight){
                if(isLight) {
                    restOfBoard.append(SET_BG_COLOR_GREEN);
                } else {
                    restOfBoard.append(SET_BG_COLOR_DARK_GREEN);
                }
            } else {
                drawSquareColor(restOfBoard, isLight);
            }

        } else {
            drawSquareColor(restOfBoard, isLight);
        }
    }

    private void drawSquareColor(StringBuilder restOfBoard, boolean isLight){
        if (isLight) {
            restOfBoard.append(SET_BG_COLOR_LIGHT_TAN);
        } else {
            restOfBoard.append(SET_BG_COLOR_DARK_TAN);
        }
    }

    private String drawFirstAndLastRow(boolean black){
        if(black) {
            return SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " h " + " g " + " f " + " e " + " d " + " c " + " b "
                    + " a " + "   " + RESET_BG_COLOR + EMPTY;
        } else{
            return SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " a " + " b " + " c " + " d " + " e "
                    + " f " + " g " + " h " + "   " + RESET_BG_COLOR + EMPTY;
        }
    }

    private void drawRowLabel(StringBuilder restOfBoard, int count) {
        restOfBoard.append(SET_BG_COLOR_BLUE).append(" ").append(count).append(" ");
    }

    private void drawPieces(ChessBoard board, int i, int j, StringBuilder restOfBoard){
        if (board.isPieceOnSquare(i + 1, j + 1)) {
            ChessPiece currentChessPiece = board.getPiece(new ChessPosition(i + 1, j + 1));
            restOfBoard.append(chessPieceToDraw(currentChessPiece));
        } else {
            restOfBoard.append("   ");
        }
    }



    private String chessPieceToDraw(ChessPiece chessPiece){
        String piece = "";
        if(chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            switch (chessPiece.getPieceType()){
                case PAWN -> piece = BLACK_PAWN;
                case ROOK -> piece = BLACK_ROOK;
                case KNIGHT -> piece = BLACK_KNIGHT;
                case BISHOP -> piece = BLACK_BISHOP;
                case QUEEN -> piece = BLACK_QUEEN;
                case KING -> piece = BLACK_KING;
            }
        } else {
            switch (chessPiece.getPieceType()){
                case PAWN -> piece = WHITE_PAWN;
                case ROOK -> piece = WHITE_ROOK;
                case KNIGHT -> piece = WHITE_KNIGHT;
                case BISHOP -> piece = WHITE_BISHOP;
                case QUEEN -> piece = WHITE_QUEEN;
                case KING -> piece = WHITE_KING;
            }
        }
        return piece;
    }

}

