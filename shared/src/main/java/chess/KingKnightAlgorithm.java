package chess;

import java.util.ArrayList;

public class KingKnightAlgorithm {
    public static void moveAlgorithm(int[][] differentWays, ArrayList<ChessMove> potentialMoves, ChessBoard board, ChessPosition myPosition){
        for(int[] pair : differentWays){
            potentialMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + pair[0],
                    myPosition.getColumn() + pair[1]), null));
        }
        potentialMoves.removeIf(move -> (move.getEndPosition().getColumn() > 8) ||
                (move.getEndPosition().getColumn() < 1) || (move.getEndPosition().getRow() > 8) ||
                (move.getEndPosition().getRow() < 1));

        potentialMoves.removeIf(move -> {
            ChessPiece other = board.getPiece(move.getEndPosition());
            ChessPiece current = board.getPiece(move.getStartPosition());
            return other != null && current.getTeamColor() == other.getTeamColor();
        });
    }
}
