package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    public static RegisterResult register(RegisterRequest request){
        MemoryUserDAO userAccessObject = new MemoryUserDAO();
        MemoryAuthDAO authAccessObject = new MemoryAuthDAO();
        if(request.username()== null || request.password()== null || request.email()== null){
            return new RegisterResult(null, null, "Error: (username, password and/or email cannot be empty)");
        }
        if(userAccessObject.containsUsername(request.username())){
            return new RegisterResult(null, null, "Error: already taken");
        }
        UserData userModel = new UserData(request.username(), request.password(), request.username());
        userAccessObject.createUser(userModel);
        AuthData authModel = new AuthData(null, request.username());
        String authToken = authAccessObject.createAuth(authModel);
        return new RegisterResult(request.username(), authToken, null);
    }

    public static LoginResult login(LoginRequest request) throws  IllegalArgumentException, DataAccessException{
        MemoryUserDAO userAccessObject = new MemoryUserDAO();
        MemoryAuthDAO authAccessObject = new MemoryAuthDAO();
        if(request.username()== null || request.password()== null){
            return new LoginResult(null, null, "Error: (username and/or password cannot be empty)");
        }
        if(!(userAccessObject.authenticateUser(request.username(), request.password()))){
            return  new LoginResult(null, null, "Error: unauthorized");
        }
        AuthData authModel = new AuthData(null, request.username());
        String authToken = authAccessObject.createAuth(authModel);
        return  new LoginResult(request.username(), authToken, null);

    }

    public static LogoutResult logout(LogoutRequest request) throws IllegalArgumentException{
        MemoryAuthDAO authAccessObject = new MemoryAuthDAO();
        if(request.authToken() == null){
            return new LogoutResult("Error: (authToken cannot be empty)");
        }
        try {
            authAccessObject.deleteAuth(request.authToken());
        } catch (DataAccessException e){
            return  new LogoutResult("Error: unauthorized");
        }
        return  new LogoutResult(null);

    }



}
