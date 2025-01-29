package chess;

import java.util.ArrayList;

public class ChessPositions {
    private ChessGame game;
    private ArrayList<ChessPosition> blackPositions;
    private ArrayList<ChessPosition> whitePositions;
    private ChessPosition whiteKing;
    private ChessPosition blackKing;
    ChessPositions(ChessGame game){
        this.game = game;
        blackPositions = this.game.getBlackPositions();
        whitePositions = this.game.getWhitePositions();
        whiteKing = this.game.getWhiteKing();
        blackKing = this.game.getBlackKing();
    }

    public ArrayList<ChessPosition> piecesWithKingInPath(ChessGame.TeamColor current_team) {
        ChessPosition king;
        ArrayList<ChessPosition> piecesInPath = new ArrayList<>();
        if (current_team == ChessGame.TeamColor.BLACK) {
            king = blackKing;
            for (ChessPosition piecePos : whitePositions) {
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>) game.getBoard().getPiece(piecePos).pieceMoves(game.getBoard(), piecePos);
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(king)) {
                        piecesInPath.add(piecePos);
                        break;
                    }
                }
            }
        } else{
            king = whiteKing;
            for(ChessPosition piecePos : blackPositions){
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>) game.getBoard().getPiece(piecePos).pieceMoves(game.getBoard(), piecePos);
                for(ChessMove move : moves){
                    if(move.getEndPosition().equals(king)){
                        piecesInPath.add(piecePos);
                        break;
                    }
                }
            }
        }
        
        return piecesInPath;
    }
}
