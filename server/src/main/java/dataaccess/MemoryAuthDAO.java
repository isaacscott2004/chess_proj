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
    public void deleteAuth(String authToken) {
        for(AuthData data : authDataStorage){
            if(data.getAuthToken().equals(authToken)){
                authDataStorage.remove(data);
                break;
            }
        }
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

    @Override
    public Collection<AuthData> getAuthDataStorage(){
        return authDataStorage;
    }
    @Override
    public void deleteAuthData(String username){
        authDataStorage.removeIf(data -> data.getUsername().equals(username));
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
