package client;

import dataaccess.DataAccessException;

public abstract class Client {
    public static boolean calledHelp = false;
    public abstract String help();
    public abstract String eval(String input);
    public String login(String ... params) throws DataAccessException { return "";}
    public String register(String ... params) throws DataAccessException{return "";}
    public String logout()throws DataAccessException {return "";}
    public String createGame(String ... params) throws DataAccessException {return "";}
    public String listGames()throws DataAccessException {return "";}
    public String playGame(String ... params)throws DataAccessException {return "";}
    public String observeGame(String ... params) throws DataAccessException {return "";}


    }





