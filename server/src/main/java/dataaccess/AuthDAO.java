package dataaccess;
import model.AuthData;

import java.util.Collection;


public interface AuthDAO {
    String createAuth(AuthData data);

    void deleteAuth(String authToken);

    void getAuth(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    void clearAuthdata();

    Collection<AuthData> getAuthDataStorage();

    void deleteAuthData(String username);






}
