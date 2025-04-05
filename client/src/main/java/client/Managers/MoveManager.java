package client.Managers;

import chess.ChessMove;

public class MoveManager {
    private static ChessMove move;

    public static ChessMove getMove() {
        return move;
    }

    public static void setMove(ChessMove move) {
        MoveManager.move = move;
    }
}
