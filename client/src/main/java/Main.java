import chess.*;
import client.ClientType;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var serverURL = "http://localhost:8080";
        if (args.length == 1){
            serverURL = args[0];
        }
        new Repl(serverURL, ClientType.PREL).run();
    }

}