package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTests {
    private static UserDAO userAccessObject;
    private static Server.MemoryType memoryType;
    private static UserData userDataOne;
    private static UserData userDataTwo;
    private static UserData userDataThree;
    private static UserData nullUserData;
    private static String[] dropStatements;

    @BeforeAll
    static void setupOnce(){
        memoryType = Server.MemoryType.SQL_MEMORY;
        HashMap<String, Object> daos = DAOTestUtilities.chooseMemoryType(memoryType);
        userAccessObject = (UserDAO) daos.get("user");
        dropStatements = DAOTestUtilities.dropStatements;
    }
    @BeforeEach
    void setup() throws DataAccessException {
        if(memoryType == Server.MemoryType.SQL_MEMORY) {
            DAOTestUtilities.dropTables(dropStatements);
            new MySqlDAO();
        }else{
            userAccessObject.clearUserData();
        }
        userDataOne = new UserData("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        userDataTwo = new UserData("Messi", "Barcelona", "MessiIsTheGOAT@GOAT.com");
        userDataThree = new UserData("Ronaldo", "Madrid", "RonaldoIsNotTheGOAT@NotTheGOAT.com");
        nullUserData = new UserData();
    }

    @Test
    void testAddUserSuccess() throws DataAccessException {
        assertTrue(userAccessObject.getUserDataStorage().isEmpty());
        userAccessObject.addUser(userDataOne);
        assertEquals(1, userAccessObject.getUserDataStorage().size());
    }
    @Test
    void testAddUserNullUser() {
            assertThrows(DataAccessException.class, () ->
                    userAccessObject.addUser(nullUserData));
    }

    @Test
    void testAuthenticateUserSuccess() throws DataAccessException {
        userAccessObject.addUser(userDataOne);
        assertTrue(userAccessObject.authenticateUser("Isaac", "Soccer"));
    }
    @Test
    void testAuthenticateUserFailure() throws DataAccessException{
        userAccessObject.addUser(userDataTwo);
        assertFalse(userAccessObject.authenticateUser("Bob", "1234"));
    }

    @Test
    void testClearUserData() throws DataAccessException {
        userAccessObject.addUser(userDataOne);
        userAccessObject.addUser(userDataTwo);
        userAccessObject.addUser(userDataThree);
        assertEquals(3, userAccessObject.getUserDataStorage().size());
        userAccessObject.clearUserData();
        assertTrue(userAccessObject.getUserDataStorage().isEmpty());
    }

    @Test
    void testContainsUsernameSuccess() throws DataAccessException {
        userAccessObject.addUser(userDataOne);
        assertTrue(userAccessObject.containsUsername("Isaac"));
    }

    @Test
    void testContainsUsernameFailure() throws DataAccessException {
        userAccessObject.addUser(userDataTwo);
        assertFalse(userAccessObject.containsUsername("Ronaldo"));
    }

    @Test
    void testGetUserDataStorage() throws DataAccessException {
        userAccessObject.addUser(userDataOne);
        userAccessObject.addUser(userDataTwo);
        userAccessObject.addUser(userDataThree);
        ArrayList<UserData> userData = new ArrayList<>(userAccessObject.getUserDataStorage());
        assertEquals(3, userData.size());
        ArrayList<String> expectedNames = new ArrayList<>();
        expectedNames.add("Isaac");
        expectedNames.add("Messi");
        expectedNames.add("Ronaldo");
        for(UserData data : userData){
            String name = data.getUsername();
            assertTrue(expectedNames.contains(name));
        }

    }

    @Test
    void testDeleteUserDataSuccess() throws DataAccessException {
        userAccessObject.addUser(userDataOne);
        userAccessObject.addUser(userDataTwo);
        assertEquals(2, userAccessObject.getUserDataStorage().size());
        userAccessObject.deleteUserData("Isaac");
        assertEquals(1, userAccessObject.getUserDataStorage().size());
    }

    @Test
    void deleteUserDataThrowsDataAccessException() throws DataAccessException {
        userAccessObject.addUser(userDataOne);
        userAccessObject.addUser(userDataTwo);
        assertEquals(2, userAccessObject.getUserDataStorage().size());
        assertThrows(DataAccessException.class, () ->
                userAccessObject.deleteUserData("Bob"));
        assertEquals(2, userAccessObject.getUserDataStorage().size());

    }


}
