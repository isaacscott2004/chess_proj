package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage{
    private ChessGame game;

    private LoadGameMessage() {
        super(ServerMessageType.LOAD_GAME);
    }

    public LoadGameMessage(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;

    }
    public ChessGame getGame(){
        return this.game;
    }
    public String toString(){
        return new Gson().toJson(this);
    }
}
