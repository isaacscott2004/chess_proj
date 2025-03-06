package service;

import dataaccess.*;
import server.Server;

import java.util.HashMap;

public class ServiceTestUtilities {
    static HashMap<String, Object> chooseMemoryType(Server.MemoryType memoryType){
        HashMap<String, Object> daos = new HashMap<>();
        UserDAO userDataAccessObject;
        AuthDAO authDataAccessObject;
        GameDAO gameDataAccessObject;
        if(memoryType == Server.MemoryType.IN_MEMORY){
            userDataAccessObject = new MemoryUserDAO();
            authDataAccessObject = new MemoryAuthDAO();
            gameDataAccessObject = new MemoryGameDAO();
        }
        else{
            userDataAccessObject = new MySqlUserDAO();
            authDataAccessObject = new MySqlAuthDAO();
            gameDataAccessObject = new MySqlGameDAO();
        }
        daos.put("auth", authDataAccessObject);
        daos.put("user", userDataAccessObject);
        daos.put("game", gameDataAccessObject);
        return daos;

    }
}
