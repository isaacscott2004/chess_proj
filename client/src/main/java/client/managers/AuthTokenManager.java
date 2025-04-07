package client.managers;

public class AuthTokenManager {
    private static String authToken;

    public static String getAuthToken(){
        return authToken;
    }

    public static void setAuthToken(String otherAuthToken){
        authToken = otherAuthToken;

    }
    public static void clearAuthToken(){
        authToken = null;
    }


}
