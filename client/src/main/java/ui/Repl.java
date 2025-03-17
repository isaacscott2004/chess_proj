package ui;
import client.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private ClientType type;
    private final String serverURL;

    public Repl(String serverURL, ClientType type){
        this.type = type;
        this.serverURL = serverURL;

    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            Client client;
            if (type == ClientType.PREL) {
                client = createClient(serverURL, ClientType.PREL);
                System.out.println(client.help());
                String result;
                while (true) {
                    printPrompt();
                    String line = scanner.nextLine();
                    System.out.println(RESET_TEXT_COLOR);
                    try {
                        result = client.eval(line);
                        if(result.contains("Error")){
                            System.out.print(SET_TEXT_COLOR_RED + result);
                        }
                        else if(Client.calledHelp){
                            System.out.println(SET_TEXT_BOLD + result);
                            System.out.println(RESET_TEXT_BOLD_FAINT);
                            Client.calledHelp = false;
                        }
                        else {
                            System.out.print(SET_TEXT_COLOR_BLUE + result);
                        }
                        System.out.println(RESET_TEXT_COLOR);
                        if ((line.toLowerCase().contains("login") || line.toLowerCase().contains("register")) && !result.contains("Error")) {
                            type = ClientType.POSTL;
                            break;
                        }
                        else if(line.contains("quit")){
                            break;
                        }
                    } catch (Throwable e) {
                        var msg = e.toString();
                        System.out.print(msg);
                    }
                }
                if (result.equals("You have quit the program, bye.")) {
                    break;
                }
                System.out.println();
            }
            if (type == ClientType.POSTL) {
                client = createClient(serverURL, ClientType.POSTL);
                System.out.println(client.help());
                String result;
                while (true) {
                    printPrompt();
                    String line = scanner.nextLine();
                    System.out.println(RESET_TEXT_COLOR);
                    try {
                        result = client.eval(line);
                        if(result.contains("Error")){
                            System.out.print(SET_TEXT_COLOR_RED + result);
                        }
                        else if(Client.calledHelp){
                            System.out.println(SET_TEXT_BOLD + result);
                            System.out.println(RESET_TEXT_BOLD_FAINT);
                            Client.calledHelp = false;
                        }
                        else {
                            System.out.print(SET_TEXT_COLOR_BLUE + result);
                        }
                        System.out.println(RESET_TEXT_COLOR);
                        if (line.toLowerCase().contains("logout")) {
                            type = ClientType.PREL;
                            break;
                        }
                    } catch (Throwable e) {
                        var msg = e.toString();
                        System.out.print(msg);
                    }

                }

                System.out.println();
            }
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
        System.out.print("\n" + RESET + ">>> ");
    }






}
