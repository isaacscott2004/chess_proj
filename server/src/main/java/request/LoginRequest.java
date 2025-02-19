package request;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public record LoginRequest(String username, String password, AuthDAO authAccessObject, UserDAO userAccessObject) {
}
