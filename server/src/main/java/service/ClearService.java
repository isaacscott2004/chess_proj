package service;

import dataaccess.*;
import request.ClearRequest;
import result.ClearResult;

public class ClearService {
    public static ClearResult clear(AuthDAO authAccessObject, UserDAO userAccessObject,  GameDAO gameAccessObject){
        authAccessObject.clearAuthdata();
        userAccessObject.clearUserData();
        gameAccessObject.clearGameData();
        return new ClearResult("Db successfully cleared");

    }
}
