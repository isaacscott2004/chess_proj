package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService{
    /**
     * registers a new user
     * @param request a RegisterRequest object with a username, password, and email
     * @param authAccessObject access to AuthData
     * @param userAccessObject access to UserData
     * @return A RegisterResult object with the username, an authToken and a null message
     * @throws BadRequestException if the username and/or password and/or email is null
     * @throws AlreadyTakenException if the username is already taken
     */
    public static RegisterResult register(RegisterRequest request, AuthDAO authAccessObject, UserDAO userAccessObject)
            throws BadRequestException, AlreadyTakenException, DataAccessException {
        if(request.username()== null || request.password()== null || request.email()== null){
            throw new BadRequestException("Error: (username and/or password and/or email cannot be empty)");
        }
        if(userAccessObject.containsUsername(request.username())){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData userModel = new UserData(request.username(), request.password(), request.email());
        userAccessObject.addUser(userModel);
        AuthData authModel = new AuthData(null, request.username());
        String authToken = authAccessObject.createAuth(authModel);
        return new RegisterResult(request.username(), authToken, null);
    }

    /**
     * log's an existing user in
     * @param request a LoginRequest object with a username and a password
     * @param authAccessObject access to AuthData
     * @param userAccessObject access to UserData
     * @return a LoginResult object with the username, an authToken and a null message
     * @throws BadRequestException if the username and/or password is null
     * @throws UnauthorizedException if the authToken is not authorized
     */
    public static LoginResult login(LoginRequest request, AuthDAO authAccessObject, UserDAO userAccessObject)
            throws BadRequestException, UnauthorizedException, DataAccessException {
        if(request.username()== null || request.password()== null){
            throw new BadRequestException("Error: (username and/or password cannot be empty)");
        }
        if(!(userAccessObject.authenticateUser(request.username(), request.password()))){
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authModel = new AuthData(null, request.username());
        String authToken = authAccessObject.createAuth(authModel);
        return  new LoginResult(request.username(), authToken, null);

    }

    /**
     * log's and existing user out
     * @param authToken the token to be authorized
     * @param authAccessObject access to AuthData
     * @return a LogoutResult object with a message
     * @throws UnauthorizedException if the authToken is unauthorized
     */
    public static LogoutResult logout(String authToken, AuthDAO authAccessObject)throws UnauthorizedException {
        try {
            authAccessObject.deleteAuth(authToken);
        } catch (DataAccessException e){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new LogoutResult("Logout successful!");
    }




}
