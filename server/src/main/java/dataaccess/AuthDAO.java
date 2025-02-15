package dataaccess;
import model.AuthData;

public interface AuthDAO {
    public String createAuth(AuthData data) throws DataAccessException;

    public void deleteAuth(String authToken)throws DataAccessException;

    public void getAuth(String authToken) throws DataAccessException;

    public String getUsername(String authToken) throws DataAccessException;

    public void clearAuthdata();




}
