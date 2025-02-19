package chess;

import java.util.ArrayList;

public class QueenBishopRookAlgorithm {
    public static void moveAlgorithm(int[][] differentWays, ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> potentialMoves) {
        for (int[] pair : differentWays) {
            int row = pair[0];
            int col = pair[1];
            ChessPosition currentPosition = myPosition;
            while (true) {
                currentPosition = new ChessPosition(currentPosition.getRow() + row, currentPosition.getColumn() + col);
                if ((currentPosition.getRow() > 8) || (currentPosition.getRow() < 1) ||
                        (currentPosition.getColumn() > 8) || (currentPosition.getColumn() < 1)) {
                    break;
                }
                ChessPiece current = board.getPiece(myPosition);
                ChessPiece other = board.getPiece(currentPosition);
                if (other != null && current.getTeamColor() == other.getTeamColor()) {
                    break;
                } else if (other != null && current.getTeamColor() != other.getTeamColor()) {
                    potentialMoves.add(new ChessMove(myPosition, currentPosition, null));
                    break;
                }
                potentialMoves.add(new ChessMove(myPosition, currentPosition, null));
            }
        }
    }
}
