package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO{
    private Collection<UserData> userDataStorage;

    public MemoryUserDAO(){
        userDataStorage = new ArrayList<>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for(UserData data : userDataStorage){
            if(data.getUsername().equals(username)){
                return data;
            }
        }
        throw new DataAccessException("no user data with specified username");
    }

    @Override
    public void createUser(UserData user) {
        userDataStorage.add(user);

    }

    @Override
    public boolean authenticateUser(String username, String password) {
        for(UserData data : userDataStorage){
            if(data.getUsername().equals(username) && data.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearUserData() {
        userDataStorage.clear();

    }
}
