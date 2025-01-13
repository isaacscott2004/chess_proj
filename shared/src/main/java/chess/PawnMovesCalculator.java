package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (myPosition.getRow() == 7) {
                if (!(board.isPieceOnSquare(myPosition.getRow() - 1, myPosition.getColumn()))) {
                    potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
                    if (!(board.isPieceOnSquare(myPosition.getRow() - 2, myPosition.getColumn()))) {
                        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()), null));
                    }
                }

                capture(potentialMoves, -1, -1, myPosition, board, false);
                capture(potentialMoves, -1, 1, myPosition, board, false);
            } else if (myPosition.getRow() == 2) {
                promotion(potentialMoves, -1, 0, myPosition);
                potentialMoves.removeIf(move -> board.isPieceOnSquare(move.getStartPosition().getRow() - 1, move.getStartPosition().getColumn()));
                capture(potentialMoves, -1, -1, myPosition, board, true);
                capture(potentialMoves, -1, 1, myPosition, board, true);
            } else {
                potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
                potentialMoves.removeIf(move -> board.isPieceOnSquare(move.getStartPosition().getRow() - 1, move.getStartPosition().getColumn()));
                capture(potentialMoves, -1, -1, myPosition, board, false);
                capture(potentialMoves, -1, 1, myPosition, board, false);
            }
        } else {
            if (myPosition.getRow() == 2) {
                if (!(board.isPieceOnSquare(myPosition.getRow() + 1, myPosition.getColumn()))) {
                    potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
                    if (!(board.isPieceOnSquare(myPosition.getRow() + 2, myPosition.getColumn()))) {
                        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()), null));
                    }
                }


                capture(potentialMoves, 1, -1, myPosition, board, false);
                capture(potentialMoves, 1, 1, myPosition, board, false);

            } else if (myPosition.getRow() == 7) {
                promotion(potentialMoves, 1, 0, myPosition);
                potentialMoves.removeIf(move -> board.isPieceOnSquare(move.getStartPosition().getRow() + 1, move.getStartPosition().getColumn()));
                capture(potentialMoves, 1, -1, myPosition, board, true);
                capture(potentialMoves, 1, 1, myPosition, board, true);
            } else {
                potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
                potentialMoves.removeIf(move -> board.isPieceOnSquare(move.getStartPosition().getRow() + 1, move.getStartPosition().getColumn()));
                capture(potentialMoves, 1, -1, myPosition, board, false);
                capture(potentialMoves, 1, 1, myPosition, board, false);
            }

        }

        return potentialMoves;
    }

    private void promotion(ArrayList<ChessMove> potentialMoves, int rowDirection, int colDirection, ChessPosition myPosition) {
        ArrayList<ChessPiece.PieceType> types = new ArrayList<>();
        types.add(ChessPiece.PieceType.QUEEN);
        types.add(ChessPiece.PieceType.ROOK);
        types.add(ChessPiece.PieceType.BISHOP);
        types.add(ChessPiece.PieceType.KNIGHT);
        for (ChessPiece.PieceType type : types) {
            potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection), type));
        }
    }

    private void capture(ArrayList<ChessMove> potentialMoves, int rowDirection, int colDirection, ChessPosition myPosition, ChessBoard board, boolean promotion) {
        if (promotion) {
            if (board.isPieceOnSquare(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection) &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                promotion(potentialMoves, rowDirection, colDirection, myPosition);
            }

        } else {
            if (board.isPieceOnSquare(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection) &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + rowDirection, myPosition.getColumn() + colDirection), null));
            }
        }

    }
}
