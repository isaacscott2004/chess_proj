package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> potentialMoves = new ArrayList<>();
        int[][] differentWays = {{1, -1}, {-1, 1}, {1, 1}, {-1, -1}};
        for(int[] pair : differentWays){
            int row = pair[0];
            int col = pair[1];
            ChessPosition currentPosition = myPosition;
            while(true){
                currentPosition = new ChessPosition(currentPosition.getRow() + row, currentPosition.getColumn() + col);
                if ((currentPosition.getRow() > 8) || (currentPosition.getRow() < 1) || (currentPosition.getColumn() > 8) || (currentPosition.getColumn() < 1)){
                    break;
                }
                potentialMoves.add(new ChessMove(myPosition, currentPosition, null));
            }
        }


        return potentialMoves;


        }

}
