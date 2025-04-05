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

            if(type == ClientType.GAME){
                handleSession(scanner, ClientType.GAME);
            }
        }
    }

    private boolean handleSession(Scanner scanner, ClientType clientType) {
        boolean breakOut = false;
        Client client = createClient(serverURL, type);

        if(!(type == ClientType.GAME )) {
            System.out.println(client.help());
        }
        String result = null;

        while (true) {
            if(type != ClientType.GAME) {
                printPrompt();
            }
            else if(result != null && result.contains(INVISIBLESEPERATOR)){
                printPrompt();
            }

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
                if(clientType == ClientType.POSTL && shouldTransitionToGame(line, result)){
                    type = ClientType.GAME;
                    break;
                }
                if(clientType == ClientType.GAME && shouldTransitionToPostl(line, result)){
                    type = ClientType.POSTL;
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
        return (line.toLowerCase().contains("login") || line.toLowerCase().contains("register") || line.toLowerCase().contains("leave")) && !result.contains("Error");
    }

    private boolean shouldTransitionToPrel(String line) {
        return line.toLowerCase().contains("logout");
    }

    private boolean shouldTransitionToGame(String line, String result){
        return (line.toLowerCase().contains("play") || line.toLowerCase().contains("observe")) &&!result.contains("Error");
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
        String strMessage = gson.toJson(message);

        JsonObject jsonMessage = JsonParser.parseString(strMessage).getAsJsonObject();
        String messageType = jsonMessage.get("serverMessageType").getAsString();

        switch (messageType) {
            case "LOAD_GAME" -> {
                JsonObject jsonGame = jsonMessage.getAsJsonObject("game");
                ChessGame chessGame = gson.fromJson(jsonGame, ChessGame.class);
                ChessBoard chessBoard = chessGame.getBoard();
                GameManager.setBoard(chessBoard);
                GameManager.setGame(chessGame);
                ChessGame.TeamColor currentColor = chessGame.getTeamTurn();
                ChessBoardRep chessBoardRep = new ChessBoardRep();
                System.out.println("\r" + chessBoardRep.drawBoard(GameManager.getColor(),
                        chessBoard, false, null) + "\n" + SET_TEXT_COLOR_BROWN +  currentColor
                        + "'s move" + RESET_TEXT_COLOR);

            }
            case "NOTIFICATION" -> {
                String notification = jsonMessage.get("message").getAsString();
                System.out.println("\r" + RESET + SET_TEXT_COLOR_MAGENTA + "-- " + notification + RESET_TEXT_COLOR);

            }
            case "ERROR" -> {
                String error = jsonMessage.get("errorMessage").getAsString();
                System.out.println("\r" + RESET + SET_TEXT_COLOR_RED + "Error: " + error + RESET_TEXT_COLOR);

            }
        }
        printPrompt();

    }
}