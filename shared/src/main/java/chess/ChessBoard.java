package chess;

import java.lang.reflect.Array;
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
    public ChessBoard() {
        board = new ChessPiece[8][8];
        locations = new boolean[8][8];
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
        locations[position.getRow() - 1][position.getColumn() - 1] = true;
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
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, piece);
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            i++;


        }
    }


    public boolean isPieceOnSquare(int row, int col){
        try{
            return locations[row - 1][col - 1];
        } catch (IndexOutOfBoundsException e){
            return false;
        }

    }

    public ChessPiece[][] getBoard(){
        return this.board;
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


