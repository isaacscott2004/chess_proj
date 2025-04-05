package client.Managers;

public class ObserverManager {
    private static boolean isObserver = false;

    public static boolean isIsObserver() {
        return isObserver;
    }

    public static void setIsObserver(boolean isObserver) {
        ObserverManager.isObserver = isObserver;
    }
}
