package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();

        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()), null));
        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null));
        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn() + 1), null));
        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn() - 1), null));
        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn() + 1), null));
        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()), null));
        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1), null));
        potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1), null));

        potentialMoves.removeIf(move -> (move.getEndPosition().getColumn() > 8) || (move.getEndPosition().getColumn() < 1) || (move.getEndPosition().getRow() > 8) || (move.getEndPosition().getRow() < 1));
        return potentialMoves;
    }
}
