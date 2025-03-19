package ui;
import client.*;

import java.util.Scanner;
import java.util.TreeMap;

import static ui.EscapeSequences.*;

public class Repl {
    private ClientType type;
    private final String serverURL;

    public Repl(String serverURL, ClientType type){
        this.type = type;
        this.serverURL = serverURL;

    }
//    public void run() {
//        Scanner scanner = new Scanner(System.in);
//        while(true) {
//            Client client;
//            if (type == ClientType.PREL) {
//                client = createClient(serverURL, ClientType.PREL);
//                System.out.println(client.help());
//                String result;
//                while (true) {
//                    printPrompt();
//                    String line = scanner.nextLine();
//                    System.out.println(RESET_TEXT_COLOR);
//                    try {
//                        result = client.eval(line);
//                        if(result.contains("Error")){
//                            System.out.print(SET_TEXT_COLOR_RED + result);
//                        }
//                        else if(Client.calledHelp){
//                            System.out.println(SET_TEXT_BOLD + result);
//                            System.out.println(RESET_TEXT_BOLD_FAINT);
//                            Client.calledHelp = false;
//                        }
//                        else {
//                            System.out.print(SET_TEXT_COLOR_BLUE + result);
//                        }
//                        System.out.println(RESET_TEXT_COLOR);
//                        if ((line.toLowerCase().contains("login") || line.toLowerCase().contains("register")) && !result.contains("Error")) {
//                            type = ClientType.POSTL;
//                            break;
//                        }
//                        else if(line.contains("quit")){
//                            break;
//                        }
//                    } catch (Throwable e) {
//                        var msg = e.toString();
//                        System.out.print(msg);
//                    }
//                }
//                if (result.equals("You have quit the program, bye.")) {
//                    break;
//                }
//                System.out.println();
//            }
//            if (type == ClientType.POSTL) {
//                client = createClient(serverURL, ClientType.POSTL);
//                System.out.println(client.help());
//                String result;
//                while (true) {
//                    printPrompt();
//                    String line = scanner.nextLine();
//                    System.out.println(RESET_TEXT_COLOR);
//                    try {
//                        result = client.eval(line);
//                        if(result.contains("Error")){
//                            System.out.print(SET_TEXT_COLOR_RED + result);
//                        }
//                        else if(Client.calledHelp){
//                            System.out.println(SET_TEXT_BOLD + result);
//                            System.out.println(RESET_TEXT_BOLD_FAINT);
//                            Client.calledHelp = false;
//                        }
//                        else {
//                            System.out.print(SET_TEXT_COLOR_BLUE + result);
//                        }
//                        System.out.println(RESET_TEXT_COLOR);
//                        if (line.toLowerCase().contains("logout")) {
//                            type = ClientType.PREL;
//                            break;
//                        }
//                    } catch (Throwable e) {
//                        var msg = e.toString();
//                        System.out.print(msg);
//                    }
//
//                }
//
//                System.out.println();
//            }
//        }
//
//
//    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (type == ClientType.PREL) {
                if(handleSession(scanner, ClientType.PREL)){
                    break;
                }
            }

            if (type == ClientType.POSTL) {
                handleSession(scanner, ClientType.POSTL);
            }
        }
    }

    private boolean handleSession(Scanner scanner, ClientType clientType){
        boolean breakOut = false;
        Client client = createClient(serverURL, type);
        System.out.println(client.help());
        String result;
            while (true) {
                printPrompt();
                String line = scanner.nextLine();
                System.out.println(RESET_TEXT_COLOR);
                try{
                    result = client.eval(line);
                    processResult(result);
                    if (clientType == ClientType.PREL && shouldTransitionToPostl(line, result)) {
                        type = ClientType.POSTL;
                        break;
                    }
                    if (clientType == ClientType.POSTL && shouldTransitionToPrel(line)) {
                        type = ClientType.PREL;
                        break;
                    }
                    if (line.contains("quit") && result.equals("You have quit the program, bye.")) {
                        breakOut = true;
                        break;
                    }

                } catch (Throwable e) {
                        var msg = e.toString();
                        System.out.print(msg);
                }

            }
            System.out.println();
            return breakOut;
    }

    private void processResult(String result){
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

    }
    private boolean shouldTransitionToPostl(String line, String result){
        return (line.toLowerCase().contains("login") || line.toLowerCase().contains("register")) && !result.contains("Error");

    }

    private boolean shouldTransitionToPrel(String line){
        return line.toLowerCase().contains("logout");
    }



    private Client createClient(String serverURL, ClientType type){
        Client client = null;
        switch (type){
            case GAME -> client = new GameClient(serverURL);
            case PREL -> client = new PreLClient(serverURL);
            case POSTL -> client = new PostLClient(serverURL);
        }
        return client;

    }
    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> ");
    }






}
