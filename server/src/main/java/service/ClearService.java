package service;

import dataaccess.*;
import result.ClearResult;

public class ClearService {
    /**
     * clears all the data in storage
     *
     * @param authAccessObject access to the authData
     * @param userAccessObject access to the userData
     * @param gameAccessObject access to the gameData
     * @return a ClearResult which contains a message
     */
    public static ClearResult clear(AuthDAO authAccessObject, UserDAO userAccessObject, GameDAO gameAccessObject) throws DataAccessException {
        authAccessObject.clearAuthdata();
        userAccessObject.clearUserData();
        gameAccessObject.clearGameData();
        return new ClearResult("Db successfully cleared");

    }
}
