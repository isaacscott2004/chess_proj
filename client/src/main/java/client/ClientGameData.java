package client;

public class ClientGameData {
    private final int number;
    private final int id;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;
    public ClientGameData(int number, int id, String whiteUsername, String blackUsername, String gameName){
        this.number = number;
        this.id = id;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }

    @Override
    public String toString() {
        return number + " : " +gameName + ", WHITE: " + whiteUsername + ", BLACK: " + blackUsername;
    }
}
