package dataaccess;

import model.AuthData;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO extends AuthDAO{
    private static final Collection<AuthData> authDataStorage = new ArrayList<>();
    /**
     * creates and adds an authToken to data
     * @param data the AuthData object for which the authToken will be added to
     * @return the authToken
     */
    @Override
    public String createAuth(AuthData data) {
        String authToken = AuthDAO.generateToken();
        data.setAuthToken(authToken);
        authDataStorage.add(data);
        return authToken;
    }
    /**
     * deletes teh AuthData object that contains the authToken
     * @param authToken token to be passed in
     * @throws DataAccessException if there is no AuthData with the matching authToken
     */
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        boolean found = false;
        for(AuthData data : authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                authDataStorage.remove(data);
                found = true;
                break;
            }
        }
        if(!(found)) {
            throw new DataAccessException("There is no authData with the matching authToken");
        }
    }
    /**
     * makes sure the passed in authToken is stored
     * @param authToken token to check
     * @throws DataAccessException if there is no AuthData with the matching authToken
     */
    @Override
    public void getAuth(String authToken) throws DataAccessException {
        for(AuthData data: authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                return;
            }
        }
        throw new DataAccessException("There is no authData with the matching authToken");
    }
    /**
     * makes sure the passed in authToken is stored and then gets the username specified with that authToken
     * @param authToken token to be passed in
     * @return username that is stored with the authToken in AuthData
     * @throws DataAccessException if there is no AuthData with the matching authToken
     */
    @Override
    public String getUsername(String authToken) throws DataAccessException {
        for(AuthData data: authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                return data.getUsername();
            }
        }
        throw new DataAccessException("There is no authData with the matching authToken");
    }

    /**
     * clears all of the AuthData
     */
    @Override
    public void clearAuthdata() {
        authDataStorage.clear();

    }
    // for testing
    /**
     * gets the data structure of the storage of the AuthData objects
     * @return the data structure
     */
    @Override
    public Collection<AuthData> getAuthDataStorage(){
        return authDataStorage;
    }
    /**
     * deletes a specific AuthData object
     * @param username the username that is stored in the to be deleted AuthData object
     */
    @Override
    public void deleteAuthData(String username){
        authDataStorage.removeIf(data -> data.getUsername().equals(username));
    }
}
