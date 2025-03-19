package ui;


import client.ResponseException;
import com.google.gson.Gson;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverURL;


    public ServerFacade(String serverURL){
        this.serverURL = serverURL;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
       String path = "/user";
       return makeRequest("POST", path, request, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        String path = "/session";
        return makeRequest("POST", path, request, LoginResult.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        String path = "/session";
        makeRequest("DELETE", path, null, LogoutResult.class, authToken);
    }

    public void createGame(CreateGameRequest request, String authToken) throws ResponseException {
        String path = "/game";
        makeRequest("POST", path, request, CreateGameResult.class, authToken);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        String path = "/game";
        return makeRequest("GET" , path, null, ListGamesResult.class, authToken);
    }
    public void playGame(JoinGameRequest request, String authToken) throws ResponseException {
        String path = "/game";
        makeRequest("PUT", path, request, JoinGameResult.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(authToken != null){
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (NullPointerException e){
            String errorMessage = switch (responseClass.getSimpleName()) {
                case "RegisterResult" -> "Error: This username already exists please choose a different one.";
                case "LoginResult" -> "Error: Your username or password is incorrect. Please try again or register to create a " +
                        "new user.";
                case "LogoutResult", "CreateGameResult", "ListGamesResult" -> "Error: You are not logged in or registered";
                case "JoinGameResult" -> "Error: The team you tried to play with is already taken. Please choose a different team\n" +
                        "type 'list' to see what teams and games are available";
                default -> "Error: Unexpected Error";
            };
                throw new ResponseException(errorMessage);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }



}
