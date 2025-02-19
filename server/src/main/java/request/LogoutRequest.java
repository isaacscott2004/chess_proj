package request;

import dataaccess.AuthDAO;

public record LogoutRequest(String authToken, AuthDAO authAccessObject) {
}
