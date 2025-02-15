package dataaccess;
import model.AuthData;

public interface AuthDAO {
    String createAuth(AuthData data) throws DataAccessException;

    void deleteAuth(String authToken)throws DataAccessException;

    void getAuth(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    void clearAuthdata();




}
