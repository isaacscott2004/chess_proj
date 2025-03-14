package client;

import dataaccess.DataAccessException;

public abstract class Client {
    public abstract String help();
    public abstract String eval(String input);
    public String quit() {return "";}
    public String login(String ... params) throws DataAccessException { return "";}
    public String register(String ... params) throws DataAccessException{return "";}
    public String logout(){return "";}
    public String createGame(String ... params){return "";}
    public String listGames(){return "";}


    }





