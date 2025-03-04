package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.List;

public class MySqlUserDAO implements UserDAO{
    @Override
    public void addUser(UserData user) throws DataAccessException {

    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        return false;
    }

    @Override
    public void clearUserData() throws DataAccessException {

    }

    @Override
    public boolean containsUsername(String username) throws DataAccessException {
        return false;
    }

    @Override
    public Collection<UserData> getUserDataStorage() throws DataAccessException {
        return List.of();
    }

    @Override
    public void deleteUserData(String username) throws DataAccessException {

    }
}
