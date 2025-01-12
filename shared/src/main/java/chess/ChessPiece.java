package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
//        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
//        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calculator;
        Collection<ChessMove> moves = null;
        switch (type) {
            case KING:
                calculator = new KingMovesCalculator();
                moves = calculator.pieceMoves(board, myPosition);
                moves.removeIf(move -> {
                    ChessPiece other = board.getPiece(move.getEndPosition());
                    return other != null && this.getTeamColor() == other.getTeamColor();
                });
                break;
            case QUEEN:
                calculator = new QueenMovesCalculator();
                moves = calculator.pieceMoves(board, myPosition);
                break;
            case BISHOP:
                calculator = new BishopMovesCalculator();
                moves = calculator.pieceMoves(board, myPosition);
                break;
            case KNIGHT:
                calculator = new KnightMovesCalculator();
                moves = calculator.pieceMoves(board, myPosition);
                moves.removeIf(move -> {
                    ChessPiece other = board.getPiece(move.getEndPosition());
                    return other != null && this.getTeamColor() == other.getTeamColor();
                });
                break;
            case ROOK:
                calculator = new RookMovesCalculator();
                moves = calculator.pieceMoves(board, myPosition);
                break;
            case PAWN:
                calculator = new PawnMovesCalculator();
                moves = calculator.pieceMoves(board, myPosition);
                break;
            }
        return moves;

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
