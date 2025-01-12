package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
