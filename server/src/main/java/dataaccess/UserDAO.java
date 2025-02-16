package dataaccess;
import model.UserData;

import java.util.Collection;

public interface UserDAO {

    void createUser(UserData user);

    boolean authenticateUser(String username, String password);

    void clearUserData();

    boolean containsUsername(String username);

    Collection<UserData> getUserDataStorage();

    void deleteUserData(String username);

}
