package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    /**
     * adds user to storage
     *
     * @param user the user to be added
     */
    void addUser(UserData user) throws DataAccessException;

    /**
     * authenticates the user by seeing if the username and password are in a UserData object
     *
     * @param username the username to check
     * @param password the password to check
     * @return true if the username and password are in a UserData object false otherwise
     */
    boolean authenticateUser(String username, String password) throws DataAccessException;

    /**
     * clears all UserData
     */
    void clearUserData() throws DataAccessException;

    /**
     * checks to see is a username is taken or not
     *
     * @param username username to check
     * @return true if username is taken false if it is not taken
     */
    boolean containsUsername(String username) throws DataAccessException;

    /**
     * gets the data structure where all the UserData objects are stored
     *
     * @return a Collection containing all the UserData objects
     */
    Collection<UserData> getUserDataStorage() throws DataAccessException;

    /**
     * deletes a specific UserData object
     *
     * @param username the username of the UserData object to be deleted
     * @throws DataAccessException if there is no UserData object with the specified username
     */
    void deleteUserData(String username) throws DataAccessException;

}
