package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO{
    private static final Collection<UserData> USER_DATA_STORAGE = new ArrayList<>();
    /**
     * adds user to storage
     * @param user the user to be added
     */
    @Override
    public void addUser(UserData user) {
        USER_DATA_STORAGE.add(user);
    }

    /**
     * authenticates the user by seeing if the username and password are in a UserData object
     * @param username the username to check
     * @param password the password to check
     * @return true if the username and password are in a UserData object false otherwise
     */
    @Override
    public boolean authenticateUser(String username, String password) {
        for(UserData data : USER_DATA_STORAGE){
            if(data.getUsername().equals(username) && data.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    /**
     * clears all UserData
     */
    @Override
    public void clearUserData() {
        USER_DATA_STORAGE.clear();

    }
    /**
     *checks to see is a username is taken or not
     * @param username username to check
     * @return true if username is taken false if it is not taken
     */
    @Override
    public boolean containsUsername(String username) {
        for(UserData data : USER_DATA_STORAGE){
            if(data.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }
    // for tests
    /**
     * gets the data structure where all the UserData objects are stored
     * @return a Collection containing all the UserData objects
     */
    @Override
    public Collection<UserData> getUserDataStorage(){
        return USER_DATA_STORAGE;
    }
    /**
     * deletes a specific UserData object
     * @param username the username of the UserData object to be deleted
     */
    @Override
    public void deleteUserData(String username){
        USER_DATA_STORAGE.removeIf(data -> data.getUsername().equals(username));

    }
}
