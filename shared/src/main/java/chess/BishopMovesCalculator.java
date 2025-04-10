package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        int[][] differentWays = {{1, -1}, {-1, 1}, {1, 1}, {-1, -1}};
        QueenBishopRookAlgorithm.moveAlgorithm(differentWays, board, myPosition, potentialMoves);
        return potentialMoves;

    }

}
