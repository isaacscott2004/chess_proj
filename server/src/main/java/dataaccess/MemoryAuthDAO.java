package dataaccess;

import model.AuthData;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO{
    private Collection<AuthData> authDataStorage;

    public MemoryAuthDAO(){
        this.authDataStorage = new ArrayList<>();
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {
        data.setAuthToken(generateToken());
        authDataStorage.add(data);
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
    public String getUsername(String authToken) throws DataAccessException {
        for(AuthData data: authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                return data.getUsername();
            }
        }
        throw new DataAccessException("There is no authData with the matching authToken");
    }

    @Override
    public void clearAuthdata() {
        authDataStorage.clear();

    }
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
