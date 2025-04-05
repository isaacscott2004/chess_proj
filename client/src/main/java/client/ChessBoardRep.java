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
                            boolean highlightBoard, ArrayList<ChessPosition> potentialFinalPositions){
        ChessPiece[][] allPieces = board.getBoard();
        boolean isLightTan;
        StringBuilder restOfBoard = new StringBuilder();
        if (color == ChessGame.TeamColor.BLACK){
            if(!highlightBoard) {
                int count = 1;
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " h " + " g " + " f " + " e " + " d " + " c " + " b "
                        + " a " + "   " + RESET_BG_COLOR + EMPTY + "\n");
                for (int i = 0; i < allPieces.length; i++) {
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ");
                    isLightTan = count % 2 != 0;
                    for (int j = 7; j >= 0; j--) {
                        if (isLightTan) {
                            restOfBoard.append(SET_BG_COLOR_LIGHT_TAN);
                        } else {
                            restOfBoard.append(SET_BG_COLOR_DARK_TAN);
                        }
                        if (board.isPieceOnSquare(i + 1, j + 1)) {
                            ChessPiece currentChessPiece = board.getPiece(new ChessPosition(i + 1, j + 1));
                            restOfBoard.append(chessPieceToDraw(currentChessPiece));
                        } else restOfBoard.append("   ");
                        isLightTan = !isLightTan;

                    }
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ")
                            .append(RESET_BG_COLOR).append(EMPTY).append("\n");
                    count++;

                }
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " h " + " g " + " f " + " e " + " d "
                        + " c " + " b " + " a " + "   " + RESET_BG_COLOR + EMPTY);
            }else{
                int count = 1;
                boolean highlight;
                ChessPosition cPosition;
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " h " + " g " + " f " + " e " + " d " + " c " + " b "
                        + " a " + "   " + RESET_BG_COLOR + EMPTY + "\n");
                for(int i = 0; i < allPieces.length; i++){
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ");
                    isLightTan = count % 2 != 0;
                    for (int j = 7; j >= 0; j--) {
                        cPosition = new ChessPosition(i + 1, j + 1);
                        highlight = potentialFinalPositions.contains(cPosition);
                        if(highlight){
                            restOfBoard.append(SET_BG_COLOR_GREEN);
                        } else if(isLightTan){
                            restOfBoard.append(SET_BG_COLOR_LIGHT_TAN);
                        } else{
                            restOfBoard.append(SET_BG_COLOR_DARK_TAN);
                        }
                        if (board.isPieceOnSquare(i + 1, j + 1)) {
                            ChessPiece currentChessPiece = board.getPiece(new ChessPosition(i + 1, j + 1));
                            restOfBoard.append(chessPieceToDraw(currentChessPiece));
                        } else restOfBoard.append("   ");
                        isLightTan = !isLightTan;

                    }
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ")
                            .append(RESET_BG_COLOR).append(EMPTY).append("\n");
                    count++;
                }
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " h " + " g " + " f " + " e " + " d "
                        + " c " + " b " + " a " + "   " + RESET_BG_COLOR + EMPTY);
            }
        }
        else {
            if (!highlightBoard) {
                int count = 8;
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " a " + " b " + " c " + " d " + " e "
                        + " f " + " g " + " h " + "   " + RESET_BG_COLOR + EMPTY + "\n");
                for (int i = 7; i >= 0; i--) {
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ");
                    isLightTan = count % 2 == 0;
                    for (int j = 0; j < allPieces[0].length; j++) {
                        if (isLightTan) {
                            restOfBoard.append(SET_BG_COLOR_LIGHT_TAN);
                        } else {
                            restOfBoard.append(SET_BG_COLOR_DARK_TAN);
                        }
                        if (board.isPieceOnSquare(i + 1, j + 1)) {
                            ChessPiece currentChessPiece = board.getPiece(new ChessPosition(i + 1, j + 1));
                            restOfBoard.append(chessPieceToDraw(currentChessPiece));
                        } else restOfBoard.append("   ");
                        isLightTan = !isLightTan;
                    }
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ")
                            .append(RESET_BG_COLOR).append(EMPTY).append("\n");
                    count--;
                }
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " a " + " b " + " c " + " d " + " e "
                        + " f " + " g " + " h " + "   " + RESET_BG_COLOR + EMPTY);
            } else{
                int count = 8;
                boolean highlight;
                ChessPosition cPosition;
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " a " + " b " + " c " + " d " + " e "
                        + " f " + " g " + " h " + "   " + RESET_BG_COLOR + EMPTY + "\n");
                for (int i = 7; i >= 0; i--) {
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ");
                    isLightTan = count % 2 == 0;
                    for (int j = 0; j < allPieces[0].length; j++) {
                        cPosition = new ChessPosition(i + 1, j + 1);
                        highlight = potentialFinalPositions.contains(cPosition);
                        if(highlight){
                            restOfBoard.append(SET_BG_COLOR_GREEN);
                        } else if(isLightTan){
                            restOfBoard.append(SET_BG_COLOR_LIGHT_TAN);
                        } else{
                            restOfBoard.append(SET_BG_COLOR_DARK_TAN);
                        }
                        if (board.isPieceOnSquare(i + 1, j + 1)) {
                            ChessPiece currentChessPiece = board.getPiece(new ChessPosition(i + 1, j + 1));
                            restOfBoard.append(chessPieceToDraw(currentChessPiece));
                        } else restOfBoard.append("   ");
                        isLightTan = !isLightTan;
                    }
                    restOfBoard.append(SET_BG_COLOR_BLUE + " ").append(count).append(" ")
                            .append(RESET_BG_COLOR).append(EMPTY).append("\n");
                    count--;

                    }
                restOfBoard.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLACK + "   " + " a " + " b " + " c " + " d " + " e "
                        + " f " + " g " + " h " + "   " + RESET_BG_COLOR + EMPTY);

                }


            }
        return restOfBoard.toString();
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
