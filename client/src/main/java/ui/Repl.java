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
            System.out.println(client.help());
            Scanner scanner = new Scanner(System.in);
            String result = "";
            while (!result.equals("quit")) {
                printPrompt();
                String line = scanner.nextLine();
                System.out.println(RESET_TEXT_COLOR);
                try {
                    result = client.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            System.out.println();
        } else if(type == ClientType.POSTL){
            System.out.println(client.help());
            Scanner scanner = new Scanner(System.in);
            String result = "";
            while (!result.equals("logout")) {
                printPrompt();
                String line = scanner.nextLine();
                System.out.println(RESET_TEXT_COLOR);
                try {
                    result = client.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e){
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
            case POSTL -> client = new PostLClient(serverURL);
        }
        return client;

    }
    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
    }






}
