package request;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public record CreateGameRequest(String gameName, String authToken, AuthDAO authAccessObject, GameDAO gameAccessObject) {
}
