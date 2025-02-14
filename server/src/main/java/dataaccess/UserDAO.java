package dataaccess;
import model.UserData;

public interface UserDAO {
    public UserData getUser (String username) throws DataAccessException;

    public void createUser(UserData user);

    public boolean authenticateUser(String username, String password);

    public void clearUserData();

    public boolean containsUsername(String username);
}
