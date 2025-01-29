package chess;

import java.util.ArrayList;
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


    public ChessGame() {
        this.team = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();



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
        Collection<ChessMove> movesBeforeChecks = piece.pieceMoves(this.board, startPosition);
        return movesBeforeChecks;
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
        ChessGame.TeamColor currentColor = getTeamTurn();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if(!(board.isPieceOnSquare(moveStartPosition.getRow(), moveStartPosition.getColumn()))){
            throw new InvalidMoveException("This move is not a valid move.");
        }
        if(board.getPiece(moveStartPosition).getTeamColor() != currentColor){
            throw new InvalidMoveException("This move is not a valid move.");
        }
        Collection<ChessMove> validMoves = validMoves(moveStartPosition);
        if(!(validMoves.contains(move))){
            throw new InvalidMoveException("This move is not a valid move.");
        }
        if(isInCheck(currentColor)){
            throw new InvalidMoveException("This move is not a valid move.");
        }
        if(board.getPiece(moveStartPosition).getPieceType() == ChessPiece.PieceType.PAWN && promotionPiece != null ){
            if (currentColor == TeamColor.BLACK && moveStartPosition.getRow() == 2 ||
                    (currentColor == TeamColor.WHITE && moveStartPosition.getRow() == 7)) {
                board.addPiece(moveEndPosition, new ChessPiece(currentColor, promotionPiece));
                board.removePiece(moveStartPosition);
            }
        } else {
            this.board.movePiece(moveStartPosition, moveEndPosition, board.getPiece(moveStartPosition));
        }
        if(currentColor == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        } else{
            setTeamTurn(TeamColor.BLACK);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return !piecesWithKingInPath(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        } if(!(getKingMoves(teamColor).isEmpty())){
            return false;
        } if(!(getMovesToProtectKing(teamColor).isEmpty())){
            return false;
        }
        return true;

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
    public ArrayList<ChessPosition> getWhitePositions(){
        return board.getWhitePieces();
    }
    public ArrayList<ChessPosition> getBlackPositions(){
        return board.getBlackPieces();
    }

    public ChessPosition getWhiteKing(){
        return board.getWhiteKing();
    }

    public ChessPosition getBlackKing(){
        return board.getBlackKing();
    }

    private ArrayList<ChessPosition> piecesWithKingInPath(ChessGame.TeamColor currentTeam) {
        ChessPosition king;
        ArrayList<ChessPosition> piecesInPath = new ArrayList<>();
        if (currentTeam == ChessGame.TeamColor.BLACK) {
            king = getBlackKing();
            for (ChessPosition piecePos : getWhitePositions()) {
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>)getBoard().getPiece(piecePos).pieceMoves(getBoard(), piecePos);
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(king)) {
                        piecesInPath.add(piecePos);
                        break;
                    }
                }
            }
        } else{
            king = getWhiteKing();
            for(ChessPosition piecePos : getBlackPositions()){
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>)getBoard().getPiece(piecePos).pieceMoves(getBoard(), piecePos);
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
    private ArrayList<ChessMove> getKingMoves(TeamColor currentTeam){
        ChessPosition king;
        ArrayList<ChessMove> kingMoves = new ArrayList<>();
        if(currentTeam == TeamColor.BLACK){
            king = getBlackKing();
        } else{
            king = getWhiteKing();
        }
        ChessPiece kingPiece = board.getPiece(king);
        ArrayList<ChessMove> allKingMoves = (ArrayList<ChessMove>) kingPiece.pieceMoves(board, king);
        for(ChessMove move : allKingMoves){
            ChessPosition newKingPosition = move.getEndPosition();
            ChessPiece otherPiece = board.getPiece(newKingPosition);
            getBoard().movePiece(king, newKingPosition, kingPiece);
            boolean stillInCheck =  !piecesWithKingInPath(currentTeam).isEmpty();
            if(!stillInCheck){
                kingMoves.add(move);
            }
            getBoard().movePiece(newKingPosition, king, kingPiece);
            getBoard().addPiece(newKingPosition, otherPiece);
        }
        return kingMoves;

    }

    private ArrayList<ChessMove> getMovesToProtectKing(TeamColor currentTeam){
        ArrayList<ChessPosition> teamList;
        ArrayList<ChessMove> savingMoves = new ArrayList<>();

        if(currentTeam == TeamColor.BLACK){
            teamList = getBlackPositions();
        } else{
            teamList = getWhitePositions();
        }
        ArrayList<ChessPosition> piecesWithKingInPath = piecesWithKingInPath(currentTeam);
        for(ChessPosition piecePos: teamList){
            ChessPiece piece = board.getPiece(piecePos);
            ArrayList<ChessMove> currentPieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(board, piecePos);

            for(ChessMove currentMove : currentPieceMoves){
                ChessPosition cMoveEndPos = currentMove.getEndPosition();

                if(piecesWithKingInPath.contains(cMoveEndPos)){
                    savingMoves.add(currentMove);
                }
            }
        }
        return savingMoves;

    }
}


