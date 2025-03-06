package dataaccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
public class MySqlAuthDAOTests {

    private static AuthDAO authAccessObject;
    private static Server.MemoryType memoryType;
    private static AuthData authDataOne;
    private static AuthData authDataTwo;
    private static AuthData authDataThree;
    private static AuthData nullAuthData;
    private static String[] dropStatements;

    @BeforeAll
    static void setupOnce(){
        memoryType = Server.MemoryType.SQL_MEMORY;
        HashMap<String, Object> daos = DAOTestUtilities.chooseMemoryType(memoryType);
        authAccessObject = (AuthDAO) daos.get("auth");
        dropStatements = new String[]{
                """
            DROP TABLE IF EXISTS auth_data
            """,
                """
            DROP TABLE IF EXISTS game_data
            """,
                """
            DROP TABLE IF EXISTS user_data
            """
        };


    }
    @BeforeEach
    void setup() throws DataAccessException {
        if(memoryType == Server.MemoryType.SQL_MEMORY) {
            DAOTestUtilities.dropTables(dropStatements);
            new MySqlDAO();
        }else{
            authAccessObject.clearAuthdata();
        }
        authDataOne = new AuthData(null, "Isaac");
        authDataTwo = new AuthData(null, "Messi");
        authDataThree = new AuthData(null, "Ronaldo");
        nullAuthData = new AuthData();

    }

    @Test
    void testCreateAuthSuccess() throws Exception {
        assertTrue(authAccessObject.getAuthDataStorage().isEmpty());
        authAccessObject.createAuth(authDataOne);
        assertNotNull(authDataOne.getAuthToken());
        assertEquals(1, authAccessObject.getAuthDataStorage().size());
    }
    @Test
    void testCreateAuthNullObject() throws Exception {
            assertTrue(authAccessObject.getAuthDataStorage().isEmpty());
            assertThrows(DataAccessException.class, () ->
                    authAccessObject.createAuth(nullAuthData));
            assertTrue(authAccessObject.getAuthDataStorage().isEmpty());
    }

    @Test
    void testDeleteAuthSuccess() throws Exception {
        authAccessObject.createAuth(authDataOne);
        authAccessObject.createAuth(authDataTwo);
        assertEquals(2, authAccessObject.getAuthDataStorage().size());
        authAccessObject.deleteAuth(authDataOne.getAuthToken());
        assertEquals(1, authAccessObject.getAuthDataStorage().size());

    }

    @Test
    void testDeleteAuthThrowsDataAccessException() throws Exception {
        authAccessObject.createAuth(authDataOne);
        assertEquals(1, authAccessObject.getAuthDataStorage().size());
        assertThrows(DataAccessException.class, () ->
                authAccessObject.deleteAuth("1234"));
        assertEquals(1, authAccessObject.getAuthDataStorage().size());
    }

    @Test
    void testGetAuthSuccess() throws Exception {
        authAccessObject.createAuth(authDataThree);
        assertDoesNotThrow(() ->
            authAccessObject.getAuth(authDataThree.getAuthToken()));
    }

    @Test
    void testGetAuthThrowsDataAccessException() throws Exception {
        authAccessObject.createAuth(authDataThree);
        assertThrows(DataAccessException.class, () ->
                authAccessObject.getAuth("1234"));
    }

    @Test
    void testGetUsernameSuccess() throws Exception {
        authAccessObject.createAuth(authDataOne);
        assertEquals("Isaac", authAccessObject.getUsername(authDataOne.getAuthToken()));
    }

    @Test
    void testGetUsernameThrowsDataAccessException() throws Exception {
        authAccessObject.createAuth(authDataOne);
        assertThrows(DataAccessException.class, () ->
                authAccessObject.getUsername("1234"));
    }

    @Test
    void testClear() throws Exception {
        authAccessObject.createAuth(authDataOne);
        authAccessObject.createAuth(authDataTwo);
        authAccessObject.createAuth(authDataThree);
        assertEquals(3, authAccessObject.getAuthDataStorage().size());
        authAccessObject.clearAuthdata();
        assertTrue(authAccessObject.getAuthDataStorage().isEmpty());

    }

    @Test
    void testGetAuthDataStorageSuccess() throws Exception {
        assertTrue(authAccessObject.getAuthDataStorage().isEmpty());
        authAccessObject.createAuth(authDataOne);
        authAccessObject.createAuth(authDataTwo);
        authAccessObject.createAuth(authDataThree);
        ArrayList<AuthData> listAuthData = new ArrayList<>(authAccessObject.getAuthDataStorage());
        assertEquals(3, listAuthData.size());
        ArrayList<String> expectedNames = new ArrayList<>();
        expectedNames.add("Isaac");
        expectedNames.add("Messi");
        expectedNames.add("Ronaldo");
        for(AuthData data : listAuthData){
            String username = data.getUsername();
            assertTrue(expectedNames.contains(username));
        }
    }

    @Test
    void testDeleteAuthDataSuccess() throws Exception {

        authAccessObject.createAuth(authDataOne);
        assertEquals(1, authAccessObject.getAuthDataStorage().size());
        authAccessObject.deleteAuthData(authDataOne.getUsername());
        assertTrue(authAccessObject.getAuthDataStorage().isEmpty());
    }

    @Test
    void testDeleteAuthDataThrowsDataAccessException() throws Exception {
            authAccessObject.createAuth(authDataOne);
            assertEquals(1, authAccessObject.getAuthDataStorage().size());
            assertThrows(DataAccessException.class, () ->
                    authAccessObject.deleteAuthData("Bob"));
            assertEquals(1, authAccessObject.getAuthDataStorage().size());
    }
}
