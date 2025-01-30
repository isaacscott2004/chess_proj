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
        return !piecesWithKingInPath(teamColor, getBoard()).isEmpty();
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
        }
        return getMovesToProtectKing(teamColor).isEmpty();

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


    private ArrayList<ChessPosition> piecesWithKingInPath(ChessGame.TeamColor currentTeam, ChessBoard board) {
        ChessPosition king;
        ArrayList<ChessPosition> piecesInPath = new ArrayList<>();
        if (currentTeam == ChessGame.TeamColor.BLACK) {
            king = board.getBlackKing();
            for (ChessPosition piecePos : board.getWhitePieces()) {
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>)board.getPiece(piecePos).pieceMoves(board, piecePos);
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(king)) {
                        piecesInPath.add(piecePos);
                        break;
                    }
                }
            }
        } else{
            king = board.getWhiteKing();
            for(ChessPosition piecePos : board.getBlackPieces()){
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>)board.getPiece(piecePos).pieceMoves(board, piecePos);
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
        ChessBoard copyBoard = CopyBoard();
        if(currentTeam == TeamColor.BLACK){
            king = copyBoard.getBlackKing();
        } else{
            king = copyBoard.getWhiteKing();
        }
        ChessPiece kingPiece = copyBoard.getPiece(king);
        ArrayList<ChessMove> allKingMoves = (ArrayList<ChessMove>) kingPiece.pieceMoves(copyBoard, king);
        for(ChessMove move : allKingMoves){
            ChessPosition newKingPosition = move.getEndPosition();
            ChessPiece otherPiece = copyBoard.getPiece(newKingPosition);
            copyBoard.movePiece(king, newKingPosition, kingPiece);
            boolean stillInCheck =  !piecesWithKingInPath(currentTeam, copyBoard).isEmpty();
            if(!stillInCheck){
                kingMoves.add(move);
            }
            copyBoard.movePiece(newKingPosition, king, kingPiece);
            copyBoard.addPiece(newKingPosition, otherPiece);
        }
        return kingMoves;

    }

    private ArrayList<ChessMove> getMovesToProtectKing(TeamColor currentTeam) {
        ArrayList<ChessPosition> teamList;
        ArrayList<ChessMove> savingMoves = new ArrayList<>();
        ChessBoard copyBoard = CopyBoard();
        if(currentTeam == TeamColor.BLACK){
            teamList = copyBoard.getBlackPieces();
        } else{
            teamList = copyBoard.getWhitePieces();
        }
        for(ChessPosition piecePos: new ArrayList<>(teamList)) {
            ChessPiece piece = copyBoard.getPiece(piecePos);
            if (piece.getPieceType() != ChessPiece.PieceType.KING) {
                ArrayList<ChessMove> currentPieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(copyBoard, piecePos);
                for (ChessMove currentMove : new ArrayList<>(currentPieceMoves)) {
                    ChessPosition newPiecePosition = currentMove.getEndPosition();
                    ChessPosition oldPiecePosition = currentMove.getStartPosition();
                    ChessPiece thisPiece = copyBoard.getPiece(oldPiecePosition);
                    ChessPiece otherPiece = copyBoard.getPiece(newPiecePosition);
                    copyBoard.movePiece(oldPiecePosition, newPiecePosition, thisPiece);
                    boolean stillInCheck = !piecesWithKingInPath(currentTeam, copyBoard).isEmpty();
                    if (!stillInCheck) {
                        savingMoves.add(currentMove);
                    }
                    copyBoard.movePiece(newPiecePosition, oldPiecePosition, thisPiece);
                    copyBoard.addPiece(newPiecePosition, otherPiece);
                }
            }
        }
        return savingMoves;
    }
    private ChessBoard CopyBoard(){
        ChessBoard copyBoard = new ChessBoard();
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                if(board.getPiece(new ChessPosition(i, j)) != null) {
                    copyBoard.addPiece(new ChessPosition(i, j), board.getPiece(new ChessPosition(i, j)));
                }
            }
        }
        return copyBoard;
    }
}


