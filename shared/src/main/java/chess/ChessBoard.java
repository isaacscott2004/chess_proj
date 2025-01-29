package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;
    private boolean[][] locations;
    private ArrayList<ChessPosition> whitePieces;
    private ArrayList<ChessPosition> blackPieces;
    private ChessPosition whiteKing;
    private ChessPosition blackKing;
    public ChessBoard() {
        this.board = new ChessPiece[8][8];
        this.locations = new boolean[8][8];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        whiteKing = null;
        blackKing = null;


    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                blackKing = position;
            } else{
                whiteKing = position;
            }
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            this.blackPieces.add(position);
            if(piece.getTeamColor() != ChessGame.TeamColor.BLACK){
                this.whitePieces.remove(position);
            }

        } else{
            this.whitePieces.add(position);
            if(piece.getTeamColor() != ChessGame.TeamColor.WHITE){
                this.blackPieces.remove(position);
            }
        }
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
        locations[position.getRow() - 1][position.getColumn() - 1] = true;

    }

    public void removePiece(ChessPosition position){
        if(getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK){
            this.blackPieces.remove(position);
        } else{
            this.whitePieces.remove(position);
        }
        board[position.getRow()-1][position.getColumn()-1] = null;
        locations[position.getRow()-1][position.getColumn()-1] = false;

    }



    public void movePiece(ChessPosition startPosition, ChessPosition endPosition, ChessPiece piece){
        addPiece(endPosition, piece);
        removePiece(startPosition);
    }


    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board = new ChessPiece[8][8];
        this.locations = new boolean[8][8];
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.blackKing = new ChessPosition(8, 5);
        this.whiteKing = new ChessPosition(1, 5);
        ArrayList<ChessPiece.PieceType> firstRow = new ArrayList<>();
        firstRow.add(ChessPiece.PieceType.ROOK);
        firstRow.add(ChessPiece.PieceType.KNIGHT);
        firstRow.add(ChessPiece.PieceType.BISHOP);
        firstRow.add(ChessPiece.PieceType.QUEEN);
        firstRow.add(ChessPiece.PieceType.KING);
        firstRow.add(ChessPiece.PieceType.BISHOP);
        firstRow.add(ChessPiece.PieceType.KNIGHT);
        firstRow.add(ChessPiece.PieceType.ROOK);
        int i = 0;
        for(ChessPiece.PieceType piece : firstRow){
            board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, piece);
            locations[7][i] = true;
            blackPieces.add(new ChessPosition(8, i + 1));
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            locations[6][i] = true;
            blackPieces.add(new ChessPosition(7 , i + 1));
            board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, piece);
            locations[0][i] = true;
            whitePieces.add(new ChessPosition(1, i + 1));
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            locations[1][i] = true;
            whitePieces.add(new ChessPosition(2, i + 1));
        i++;

        }
    }

    /**
     * checks if a piece is on a specific square
     * @param row The row of the piece to check
     * @param col The column of the piece to check
     * @return true if there is a piece on the square
     * false if there is not a piece on the square
     */
    public boolean isPieceOnSquare(int row, int col){
        try{
            return locations[row - 1][col - 1];
        } catch (IndexOutOfBoundsException e){
            return false;
        }

    }

    /**
     * a getter for the board instance variable
     * @return the board
     */
    public ChessPiece[][] getBoard(){
        return this.board;
    }

    public ArrayList<ChessPosition> getBlackPieces() {
        return blackPieces;
    }

    public ArrayList<ChessPosition> getWhitePieces() {
        return whitePieces;
    }

    public ChessPosition getWhiteKing(){
        return whiteKing;
    }

    public ChessPosition getBlackKing(){
        return blackKing;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }
}


