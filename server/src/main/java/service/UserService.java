package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

public class UserService extends AuthenticateUser {

    public static RegisterResult register(RegisterRequest request, AuthDAO authAccessObject, UserDAO userAccessObject) throws BadRequestException, AlreadyTakenException {
        if(request.username()== null || request.password()== null || request.email()== null){
            throw new BadRequestException("Error: (username, password and/or email cannot be empty)");
        }
        if(userAccessObject.containsUsername(request.username())){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData userModel = new UserData(request.username(), request.password(), request.email());
        userAccessObject.createUser(userModel);
        AuthData authModel = new AuthData(null, request.username());
        String authToken = authAccessObject.createAuth(authModel);
        return new RegisterResult(request.username(), authToken, null);
    }

    public static LoginResult login(LoginRequest request, AuthDAO authAccessObject, UserDAO userAccessObject) throws BadRequestException, UnauthorizedException {
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

    public static void logout(String authToken, AuthDAO authAccessObject) throws BadRequestException, UnauthorizedException {
        AuthenticateUser.Authenticate(authToken, authAccessObject);
        authAccessObject.deleteAuth(authToken);
    }




}
