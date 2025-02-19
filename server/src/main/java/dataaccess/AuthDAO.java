package dataaccess;
import model.AuthData;

import java.util.Collection;
import java.util.UUID;


public abstract class AuthDAO {
    /**
     * creates and adds an authToken to data
     * @param data the AuthData object for which the authToken will be added to
     * @return the authToken
     */
    public abstract String createAuth(AuthData data);

    /**
     * deletes teh AuthData object that contains the authToken
     * @param authToken token to be passed in
     * @throws DataAccessException if there is no AuthData with the matching authToken
     */
    public abstract void deleteAuth(String authToken) throws DataAccessException;

    /**
     * makes sure the passed in authToken is stored
     * @param authToken token to check
     * @throws DataAccessException if there is no AuthData with the matching authToken
     */
    public abstract void getAuth(String authToken) throws DataAccessException;

    /**
     * makes sure the passed in authToken is stored and then gets the username specified with that authToken
     * @param authToken token to be passed in
     * @return username that is stored with the authToken in AuthData
     * @throws DataAccessException if there is no AuthData with the matching authToken
     */
    public abstract String getUsername(String authToken) throws DataAccessException;

    /**
     * clears all of the AuthData
     */
    public abstract void clearAuthdata();

    /**
     * gets the data structure of the storage of the AuthData objects
     * @return the data structure
     */
    public abstract Collection<AuthData> getAuthDataStorage() throws DataAccessException;

    /**
     * deletes a specific AuthData object
     * @param username the username that is stored in the to be deleted AuthData object
     * @throws DataAccessException if there is no AuthData with the specified username
     */
    public abstract void deleteAuthData(String username)throws DataAccessException;


    /**
     * generates an authToken
     * @return the authToken
     */
    protected static String generateToken() {
        return UUID.randomUUID().toString();
    }


}
