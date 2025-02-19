package service;

import dataaccess.*;
import request.ClearRequest;

public class ClearService {
    public static void clear(ClearRequest request){
        AuthDAO authAccessObject = DAOImplmentation.getAuthDAO();
        UserDAO userAccessObject = DAOImplmentation.getUserDAO();
        GameDAO gameAccessObject = DAOImplmentation.getGameDAO();
        authAccessObject.clearAuthdata();
        userAccessObject.clearUserData();
        gameAccessObject.clearGameData();
    }
}
