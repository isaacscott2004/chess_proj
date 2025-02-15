package service;


import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTests {
    private static MemoryUserDAO userDataAccessObject;
    private static MemoryAuthDAO authDataAccessObject;
    private static Collection<AuthData> authDataList;
    private static String currentAuthToken;

    @BeforeAll
    static void setUp(){
        userDataAccessObject = new MemoryUserDAO();
        authDataAccessObject = new MemoryAuthDAO();
        authDataList =  authDataAccessObject.getAuthDataStorage();
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResult = UserService.register(registerRequest);
        currentAuthToken = registerResult.authToken();
    }

    @Test
    void testRegisterSuccess(){
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234");
        RegisterResult registerResult = UserService.register(request);
        assertEquals("Bob", registerResult.username());
        assertNotNull(registerResult.authToken());
        assertFalse(registerResult.authToken().isEmpty());
        userDataAccessObject.deleteUserData("Bob");
        authDataAccessObject.deleteAuthData("Bob");
    }

    @Test
    void testRegisterErrorNullParameter(){
        RegisterRequest request = new RegisterRequest("Billy", "1234", null);
        RegisterResult registerResult = UserService.register(request);
        assertNull(registerResult.username());
        assertNull(registerResult.authToken());
        assertEquals("Error: (username, password and/or email cannot be empty)", registerResult.message());

    }

    @Test
    void testRegisterErrorSameUsername(){
        RegisterRequest requestOne = new RegisterRequest("Joe", "1234", "bob1234");
        RegisterRequest requestTwo = new RegisterRequest("Joe", "1235", "bo1234");
        UserService.register(requestOne);
        RegisterResult registerResult = UserService.register(requestTwo);
        assertNull(registerResult.username());
        assertNull(registerResult.authToken());
        assertEquals("Error: already taken", registerResult.message());
        userDataAccessObject.deleteUserData("Joe");
        authDataAccessObject.deleteAuthData("Joe");

    }

    @Test
    void testLogoutSuccess(){
        assertEquals(1, authDataList.size());
        LogoutRequest request = new LogoutRequest(currentAuthToken);
        LogoutResult result = UserService.logout(request);
        assertTrue(authDataList.isEmpty());
        assertNull(result.message());
    }
    @Test
    void testLogoutErrorNullParameter(){
        LogoutRequest request = new LogoutRequest(null);
        LogoutResult result = UserService.logout(request);
        assertEquals("Error: (authToken cannot be empty)",result.message());
    }

    @Test
    void testLogoutErrorInvalidAuthToken(){
        LogoutRequest logoutRequest = new LogoutRequest("1234");
        LogoutResult result = UserService.logout(logoutRequest);
        assertEquals("Error: unauthorized",result.message());
    }

    @Test
    void testLoginSuccess(){
        LogoutRequest logoutRequest = new LogoutRequest(currentAuthToken);
        UserService.logout(logoutRequest);
        assertTrue(authDataList.isEmpty());
        LoginRequest request = new LoginRequest("Isaac", "Soccer");
        LoginResult result = UserService.login(request);
        currentAuthToken = result.authToken();
        assertEquals(1, authDataList.size());
        assertEquals("Isaac", request.username());
        assertNotNull(result.authToken());
        assertNull(result.message());
    }

    @Test
    void testLoginErrorNullParameter(){
        RegisterRequest registerRequest = new RegisterRequest("James", "1234", "bob1234");
        RegisterResult registerResult = UserService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        UserService.logout(logoutRequest);
        LoginRequest loginRequest =  new LoginRequest(null, "1234");
        LoginResult result = UserService.login(loginRequest);
        assertEquals("Error: (username and/or password cannot be empty)", result.message());
        assertNull(result.authToken());
        assertNull(result.username());
        userDataAccessObject.deleteUserData("James");
    }

    @Test
    void testLoginErrorInvalidPassword(){
        RegisterRequest registerRequest = new RegisterRequest("James", "1234", "bob1234");
        RegisterResult registerResult = UserService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        UserService.logout(logoutRequest);
        LoginRequest loginRequest =  new LoginRequest("James", "1111");
        LoginResult result = UserService.login(loginRequest);
        assertEquals("Error: unauthorized", result.message());
        assertNull(result.authToken());
        assertNull(result.username());
    }



}
