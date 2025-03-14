package ui;
import client.*;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;
    private final ClientType type;

    public Repl(String serverUrl, ClientType type){
        this.type = type;
        this.client = createClient(serverUrl, type);

    }
    public void run() {
        if(type == ClientType.PREL){
            System.out.println("Welcome to the Chess server. Sign in to start...");
            System.out.println(client.help());
            Scanner scanner = new Scanner(System.in);
            String result = "";
            while (!result.equals("quit")) {
                printPrompt();
                String line = scanner.nextLine();
                try {
                    result = client.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            System.out.println();

            }


        }


    private Client createClient(String serverURL, ClientType type){
        Client client = null;
        switch (type){
//            case GAME -> client = new GameClient(serverURL, this);
            case PREL -> client = new PreLClient(serverURL);
//            case POSTL -> client = new PostLClient(serverURL, this);
        }
        return client;

    }
    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
    }






}
//public class Repl implements NotificationHandler {
//    private final PetClient client;
//
//    public Repl(String serverUrl) {
//        client = new PetClient(serverUrl, this);
//    }
//
//    public void run() {
//        System.out.println("\uD83D\uDC36 Welcome to the pet store. Sign in to start.");
//        System.out.print(client.help());
//
//        Scanner scanner = new Scanner(System.in);
//        var result = "";
//        while (!result.equals("quit")) {
//            printPrompt();
//            String line = scanner.nextLine();
//
//            try {
//                result = client.eval(line);
//                System.out.print(BLUE + result);
//            } catch (Throwable e) {
//                var msg = e.toString();
//                System.out.print(msg);
//            }
//        }
//        System.out.println();
//    }
//
//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }
//
//    private void printPrompt() {
//        System.out.print("\n" + RESET + ">>> " + GREEN);
//    }
