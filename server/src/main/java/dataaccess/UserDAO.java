package dataaccess;
import model.UserData;

public interface UserDAO {

    public void createUser(UserData user);

    public boolean authenticateUser(String username, String password);

    public void clearUserData();

    public boolean containsUsername(String username);
}
