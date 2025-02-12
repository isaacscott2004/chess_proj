package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
    @Override
    public AuthData createAuth(AuthData data) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public String getUsername(String username) {
        return "";
    }

    @Override
    public void clearAuthdata() {

    }
}
