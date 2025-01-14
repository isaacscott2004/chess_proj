package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        int[][] differentWays = {{1, 0}, {0, 1}, {1, 1}, {1, -1}, {-1, 1}, {-1, 0}, {0, -1}, {-1, -1}};
        for(int[] pair : differentWays){
            potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + pair[0], myPosition.getColumn() + pair[1]), null));
        }
        potentialMoves.removeIf(move -> (move.getEndPosition().getColumn() > 8) || (move.getEndPosition().getColumn() < 1) || (move.getEndPosition().getRow() > 8) || (move.getEndPosition().getRow() < 1));

        potentialMoves.removeIf(move -> {
            ChessPiece other = board.getPiece(move.getEndPosition());
            ChessPiece current = board.getPiece(move.getStartPosition());
            return other != null && current.getTeamColor() == other.getTeamColor();
        });
        return potentialMoves;


    }
}
