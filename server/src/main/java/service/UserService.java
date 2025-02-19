package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

public class UserService {
    public static RegisterResult register(RegisterRequest request) throws OtherException, AlreadyTakenException {
        UserDAO userAccessObject = request.userAccessObject();
        AuthDAO authAccessObject = request.authAccessObject();
        if(request.username()== null || request.password()== null || request.email()== null){
            throw new OtherException("Error: (username, password and/or email cannot be empty)");
        }
        if(userAccessObject.containsUsername(request.username())){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData userModel = new UserData(request.username(), request.password(), request.email());
        userAccessObject.createUser(userModel);
        AuthData authModel = new AuthData(null, request.username());
        String authToken = authAccessObject.createAuth(authModel);
        return new RegisterResult(request.username(), authToken);
    }

    public static LoginResult login(LoginRequest request) throws OtherException, UnauthorizedException {
        UserDAO userAccessObject = request.userAccessObject();
        AuthDAO authAccessObject = request.authAccessObject();
        if(request.username()== null || request.password()== null){
            throw new OtherException("Error: (username and/or password cannot be empty)");
        }
        if(!(userAccessObject.authenticateUser(request.username(), request.password()))){
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authModel = new AuthData(null, request.username());
        String authToken = authAccessObject.createAuth(authModel);
        return  new LoginResult(request.username(), authToken);

    }

    public static void logout(LogoutRequest request)throws OtherException, UnauthorizedException {
        AuthDAO authAccessObject = request.authAccessObject();
        if(request.authToken() == null){
            throw new OtherException("Error: (authToken cannot be empty)");
        }
        try {
            authAccessObject.deleteAuth(request.authToken());
        } catch (DataAccessException e){
            throw new UnauthorizedException("Error: unauthorized");
        }

    }



}
