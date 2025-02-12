package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO{
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return false;
    }

    @Override
    public void clearUserData() {

    }
}
