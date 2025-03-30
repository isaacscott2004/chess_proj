package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private ChessGame game;
    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
    public ChessGame getGame(){
        return this.game;
    }
}
