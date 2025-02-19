package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{
@Override
public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    ArrayList<ChessMove> potentialMoves = new ArrayList<>();
    int[][] differentWays = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
    KingKnightAlgorithm.moveAlgorithm(differentWays, potentialMoves, board, myPosition);
    return potentialMoves;


}
}
