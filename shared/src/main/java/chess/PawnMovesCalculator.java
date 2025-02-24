package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (myPosition.getRow() == 7) {
                initialPosition(potentialMoves, -1, myPosition, board);
                capture(potentialMoves, -1, -1, myPosition, board, false);
                capture(potentialMoves, -1, 1, myPosition, board, false);
            } else if (myPosition.getRow() == 2) {
                endPosition(potentialMoves, -1, myPosition, board);
                capture(potentialMoves, -1, -1, myPosition, board, true);
                capture(potentialMoves, -1, 1, myPosition, board, true);
            } else {
                middlePosition(potentialMoves, -1, myPosition, board);
                capture(potentialMoves, -1, -1, myPosition, board, false);
                capture(potentialMoves, -1, 1, myPosition, board, false);
            }
        } else {
            if (myPosition.getRow() == 2) {
                initialPosition(potentialMoves, 1, myPosition, board);
                capture(potentialMoves, 1, 1, myPosition, board, false);
                capture(potentialMoves, 1, -1, myPosition, board, false);
            } else if (myPosition.getRow() == 7) {
                endPosition(potentialMoves, 1, myPosition, board);
                capture(potentialMoves, 1, 1, myPosition, board, true);
                capture(potentialMoves, 1, -1, myPosition, board, true);
            } else {
                middlePosition(potentialMoves, 1, myPosition, board);
                capture(potentialMoves, 1, 1, myPosition, board, false);
                capture(potentialMoves, 1, -1, myPosition, board, false);
            }
        }
        return potentialMoves;
    }

    private void initialPosition(ArrayList<ChessMove> potentialMoves, int rowDirection, ChessPosition myPosition, ChessBoard board) {
        if (!(board.isPieceOnSquare(myPosition.getRow() + rowDirection, myPosition.getColumn()))) {
            potentialMoves.add(new ChessMove(myPosition,
                    new ChessPosition(myPosition.getRow() + rowDirection, myPosition.getColumn()), null));
            if (!(board.isPieceOnSquare(myPosition.getRow() + (rowDirection * 2), myPosition.getColumn()))) {
                potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + (rowDirection * 2),
                        myPosition.getColumn()), null));
            }
        }
    }

    private void middlePosition(ArrayList<ChessMove> potentialMoves, int rowDirection,
                                ChessPosition myPosition, ChessBoard board) {
        if (!(board.isPieceOnSquare(myPosition.getRow() + rowDirection, myPosition.getColumn()))) {
            potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +
                    rowDirection, myPosition.getColumn()), null));
        }
    }

    private void endPosition(ArrayList<ChessMove> potentialMoves, int rowDirection, ChessPosition myPosition, ChessBoard board) {
        if (!(board.isPieceOnSquare(myPosition.getRow() + rowDirection, myPosition.getColumn()))) {
            promotion(potentialMoves, rowDirection, 0, myPosition);
        }
    }

    private void promotion(ArrayList<ChessMove> potentialMoves, int rowDirection, int colDirection, ChessPosition myPosition) {
        ArrayList<ChessPiece.PieceType> types = new ArrayList<>();
        types.add(ChessPiece.PieceType.QUEEN);
        types.add(ChessPiece.PieceType.ROOK);
        types.add(ChessPiece.PieceType.BISHOP);
        types.add(ChessPiece.PieceType.KNIGHT);
        for (ChessPiece.PieceType type : types) {
            potentialMoves.add(new ChessMove(myPosition,
                    new ChessPosition(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection), type));
        }
    }

    private void capture(ArrayList<ChessMove> potentialMoves, int rowDirection, int colDirection,
                         ChessPosition myPosition, ChessBoard board, boolean promotion) {
        if (promotion) {
            if (board.isPieceOnSquare(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection) &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + rowDirection,
                            myPosition.getColumn() + colDirection)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                promotion(potentialMoves, rowDirection, colDirection, myPosition);
            }
        } else {
            if (board.isPieceOnSquare(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection) &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + rowDirection,
                            myPosition.getColumn() + colDirection)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + rowDirection,
                        myPosition.getColumn() + colDirection), null));
            }
        }

    }
}
