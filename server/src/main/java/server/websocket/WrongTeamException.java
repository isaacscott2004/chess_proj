package server.websocket;

public class WrongTeamException extends RuntimeException {
    public WrongTeamException(String message) {
        super(message);
    }
}
