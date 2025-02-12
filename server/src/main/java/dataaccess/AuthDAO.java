package dataaccess;
import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth(AuthData data);

    public void deleteAuth(String authToken);

    public String getUsername(String username);

    public void clearAuthdata();


}
