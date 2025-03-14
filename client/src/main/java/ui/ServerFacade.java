package ui;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
import java.util.logging.Logger;

public class ServerFacade {
    private static final Logger logger = Logger.getLogger(ServerFacade.class.getName());
    private final String serverURL;


    ServerFacade(String serverURL){
        this.serverURL = serverURL;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
       String path = "/user";
       logger.info("request to register a user: " + request.username());
       return makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        String path = "/session";
        logger.info("request to login a user: " + request.username());
        return makeRequest("POST", path, request, LoginResult.class);

    }

    public LogoutResult logout() throws DataAccessException {
        String path = "/session";
        logger.info("request to logout a user");
        return makeRequest("DELETE", path, null, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        String path = "/game";
        logger.info("request to create a new game: " + request.gameName());
        return makeRequest("POST", path, request, CreateGameResult.class);
    }

    public ListGamesResult listGames() throws DataAccessException {
        String path = "/game";
        logger.info("request to list all the games");
        return makeRequest("GET" , path, null, ListGamesResult.class);
    }
    public JoinGameResult playGame(JoinGameRequest request) throws DataAccessException {
        String path = "/game";
        logger.info("request to joing an existing game: " + request.gameID());
        return makeRequest("PUT", path, request, JoinGameResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            logger.info("Making " + method + " request to: " + serverURL + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            T response =  readBody(http, responseClass);
            logger.info(method + " request was successful to " + url + ", Response: " + response);
            return response;
        } catch (Exception ex) {
            logger.severe("Request failed " + method + " " + path + ", Error: " + ex.getMessage());
            throw new DataAccessException(500, ex.getMessage());
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

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw DataAccessException.fromJson(respErr);
                }
            }

            throw new DataAccessException(status, "other failure: " + status);
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
