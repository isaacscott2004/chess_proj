package client;

import ui.ServerFacade;

public class GameClient extends Client{
    public GameClient(String serverURL){
        ServerFacade server = new ServerFacade(serverURL);
    }
    @Override
    public String help() {
        return "";
    }

    @Override
    public String eval(String input) {
        return "";

    }
}
