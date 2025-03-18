import client.ClientType;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Chess server. Register or sign in to start...\n");
        var serverURL = "http://localhost:8080";
        if (args.length == 1) {
            serverURL = args[0];
        }
        new Repl(serverURL, ClientType.PREL).run();
    }

}