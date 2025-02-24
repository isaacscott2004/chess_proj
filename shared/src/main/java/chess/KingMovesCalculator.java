package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        int[][] differentWays = {{1, 0}, {0, 1}, {1, 1}, {1, -1}, {-1, 1}, {-1, 0}, {0, -1}, {-1, -1}};
        KingKnightAlgorithm.moveAlgorithm(differentWays, potentialMoves, board, myPosition);
        return potentialMoves;


    }
}
