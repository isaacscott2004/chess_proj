package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO{
    private static final Collection<UserData> userDataStorage = new ArrayList<>();

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

    @Override
    public boolean containsUsername(String username) {
        for(UserData data : userDataStorage){
            if(data.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }
    // for tests
    @Override
    public Collection<UserData> getUserDataStorage(){
        return userDataStorage;
    }

    @Override
    public void deleteUserData(String username){
        userDataStorage.removeIf(data -> data.getUsername().equals(username));

    }
}
