package request;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public record ListGamesRequest(String authToken, AuthDAO authAccessObject, GameDAO gameAccessObject) {
}
