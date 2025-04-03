package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ui.websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final String serverURL;
    private ClientType type;

    public Repl(String serverURL, ClientType type) {
        this.type = type;
        this.serverURL = serverURL;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (type == ClientType.PREL) {
                if (handleSession(scanner, ClientType.PREL)) {
                    break;
                }
            }

            if (type == ClientType.POSTL) {
                handleSession(scanner, ClientType.POSTL);
            }
        }
    }

    private boolean handleSession(Scanner scanner, ClientType clientType) {
        boolean breakOut = false;
        Client client = createClient(serverURL, type);
        System.out.println(client.help());
        String result;
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            System.out.println(RESET_TEXT_COLOR);
            try {
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

    private void processResult(String result) {
        if (result.contains("Error")) {
            System.out.print(SET_TEXT_COLOR_RED + result);
        } else if (Client.calledHelp) {
            System.out.println(SET_TEXT_BOLD + result);
            System.out.println(RESET_TEXT_BOLD_FAINT);
            Client.calledHelp = false;
        } else {
            System.out.print(SET_TEXT_COLOR_BLUE + result);
        }
        System.out.println(RESET_TEXT_COLOR);

    }

    private boolean shouldTransitionToPostl(String line, String result) {
        return (line.toLowerCase().contains("login") || line.toLowerCase().contains("register")) && !result.contains("Error");
    }

    private boolean shouldTransitionToPrel(String line) {
        return line.toLowerCase().contains("logout");
    }


    private Client createClient(String serverURL,  ClientType type) {
        Client client = null;
        switch (type) {
            case GAME -> client = new GameClient(serverURL, this);
            case PREL -> client = new PreLClient(serverURL);
            case POSTL -> client = new PostLClient(serverURL, this);
        }
        return client;

    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> ");
    }


    @Override
    public void notify(ServerMessage message) {
        Gson gson = new Gson();
        String strMessage = message.toString();
        JsonObject jsonMessage = JsonParser.parseString(strMessage).getAsJsonObject();
        String messageType = jsonMessage.get("serverMessageType").getAsString();
        if(messageType.equals("LOAD_GAME")){
            JsonObject jsonGame = jsonMessage.getAsJsonObject("game");

            ChessGame chessGame = gson.fromJson(jsonGame, ChessGame.class);
            ChessBoard chessBoard = chessGame.getBoard();
            ChessGame.TeamColor color = chessGame.getTeamTurn();
            GameManager.setBoard(chessBoard);
            GameManager.setColor(color);

        } else if(messageType.equals("NOTIFICATION")){
            String notification = jsonMessage.get("message").getAsString();
            System.out.println(SET_TEXT_COLOR_MAGENTA + notification + RESET_TEXT_COLOR);
            printPrompt();
        } else if(messageType.equals("ERROR")){
           String error = jsonMessage.get("errorMessage").getAsString();
           System.out.println(SET_TEXT_COLOR_RED + error + RESET_TEXT_COLOR);
           printPrompt();
        }
    }
}