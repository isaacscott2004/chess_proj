package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

public class AuthenticateUser {
    public static void Authenticate (String authToken, AuthDAO authDAO) throws BadRequestException {
        if(authToken == null){
            throw new BadRequestException("Error: (authToken cannot be empty)");
        }
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e){
            throw new UnauthorizedException("Error: unauthorized");
        }

    }
}
