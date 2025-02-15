package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import request.ClearRequest;
import result.ClearResult;

public class ClearService {
    public static void clear(ClearRequest request){
        MemoryAuthDAO authAccessObject = new MemoryAuthDAO();
        MemoryUserDAO userAccessObject = new MemoryUserDAO();
        MemoryGameDAO gameAccessObject = new MemoryGameDAO();
        authAccessObject.clearAuthdata();
        userAccessObject.clearUserData();
        gameAccessObject.clearGameData();
    }
}
