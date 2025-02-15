package dataaccess;
import model.UserData;

public interface UserDAO {

    void createUser(UserData user);

    boolean authenticateUser(String username, String password);

    void clearUserData();

    boolean containsUsername(String username);
}
