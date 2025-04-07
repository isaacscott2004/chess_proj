package client.managers;

public class GameIDManager {
    private static int gameID;

    public static int getGameID(){
        return gameID;
    }

    public static void setGameID(int otherGameID){
        gameID = otherGameID;

    }
    public static void clearGameID(){
        gameID = 0;
    }
}
