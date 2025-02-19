package request;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

public record RegisterRequest(String username, String password, String email, AuthDAO authAccessObject, UserDAO userAccessObject) {
}
