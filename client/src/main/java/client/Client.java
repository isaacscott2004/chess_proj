package client;


public abstract class Client {
    public static boolean calledHelp = false;
    public abstract String help();
    public abstract String eval(String input);
    public String login(String ... params) throws ResponseException { return "";}
    public String register(String ... params) throws ResponseException{return "";}
    public String logout()throws ResponseException {return "";}
    public String createGame(String ... params) throws ResponseException {return "";}
    public String listGames()throws ResponseException {return "";}
    public String playGame(String ... params)throws ResponseException {return "";}
    public String observeGame(String ... params) throws ResponseException {return "";}


    }





