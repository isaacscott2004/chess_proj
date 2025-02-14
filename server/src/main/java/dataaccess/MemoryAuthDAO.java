package dataaccess;

import model.AuthData;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO{
    private static final Collection<AuthData> authDataStorage = new ArrayList<>();

    @Override
    public String createAuth(AuthData data) {
        String authToken = generateToken();
        data.setAuthToken(authToken);
        authDataStorage.add(data);
        return authToken;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        for(AuthData data : authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                data.setAuthToken(null);
            }
        }
        throw new DataAccessException("There is no authData with the matching authToken");
    }

    @Override
    public void getAuth(String authToken) throws DataAccessException {
        for(AuthData data: authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                return;
            }
        }
        throw new DataAccessException("There is no authData with the matching authToken");
    }

    @Override
    public void clearAuthdata() {
        authDataStorage.clear();

    }

    @Override
    public boolean validAuthToken(String authToken) {
        for(AuthData data : authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                return true;
            }
        }
        return false;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
