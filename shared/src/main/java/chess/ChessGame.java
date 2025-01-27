package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private ChessGame.TeamColor team;
    private ChessPosition whiteKing;
    private ChessPosition blackKing;

    public ChessGame() {
        this.team = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.whiteKing = new ChessPosition(1, 5);
        this.blackKing = new ChessPosition(8, 5);

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(!(board.isPieceOnSquare(startPosition.getRow(), startPosition.getColumn()))){
            return null;
        }
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(this.board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition moveStartPosition = move.getStartPosition();
        ChessPosition moveEndPosition = move.getEndPosition();
        Collection<ChessMove> validMoves = validMoves(moveStartPosition);
        if(!(validMoves.contains(move))){
            throw new InvalidMoveException("This move is not a valid move.");
        }
        if(moveStartPosition.equals(this.whiteKing)){
            this.board.movePiece(moveStartPosition, moveEndPosition, board.getPiece(moveStartPosition));
            this.whiteKing = moveEndPosition;
        } else if(moveStartPosition.equals(this.blackKing)){
            this.board.movePiece(moveStartPosition, moveEndPosition, board.getPiece(moveStartPosition));
            this.blackKing = moveEndPosition;
        }
        this.board.movePiece(moveStartPosition, moveEndPosition, board.getPiece(moveStartPosition));
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition king;
        if(this.board.getPiece(this.blackKing).getTeamColor() == teamColor){
            king = this.blackKing;
        } else{
            king = this.whiteKing;
        }

        if(!(isInCheck(teamColor))){
            return false;
        }
        if(!(validMoves(king).isEmpty())){
            return false;
        }
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;

    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
