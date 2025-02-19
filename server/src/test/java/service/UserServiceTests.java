package service;


import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.AfterAll;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class UserServiceTests {
    private static UserDAO userDataAccessObject;
    private static AuthDAO authDataAccessObject;
    private static Collection<AuthData> authDataList;
    private static String currentAuthToken;

    @BeforeAll
    static void setUp() throws OtherException {
        userDataAccessObject = new MemoryUserDAO();
        authDataAccessObject = new MemoryAuthDAO();
        authDataList =  authDataAccessObject.getAuthDataStorage();
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com",authDataAccessObject, userDataAccessObject);
        RegisterResult registerResult = UserService.register(registerRequest);
        currentAuthToken = registerResult.authToken();
    }

    @Test
    void testRegisterSuccess() throws OtherException {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234", authDataAccessObject, userDataAccessObject);
        RegisterResult registerResult = UserService.register(request);
        assertEquals("Bob", registerResult.username());
        assertNotNull(registerResult.authToken());
        assertFalse(registerResult.authToken().isEmpty());
        userDataAccessObject.deleteUserData("Bob");
        authDataAccessObject.deleteAuthData("Bob");
    }

    @Test
    void testRegisterErrorNullParameter(){
        RegisterRequest request = new RegisterRequest("Billy", "1234", null, authDataAccessObject, userDataAccessObject);
        assertThrows(OtherException.class, () -> UserService.register(request));
    }

    @Test
    void testRegisterErrorSameUsername() throws OtherException {
        RegisterRequest requestOne = new RegisterRequest("Joe", "1234", "bob1234", authDataAccessObject, userDataAccessObject);
        RegisterRequest requestTwo = new RegisterRequest("Joe", "1235", "bo1234", authDataAccessObject, userDataAccessObject);
        UserService.register(requestOne);
        assertThrows(AlreadyTakenException.class, () -> UserService.register(requestTwo));
        userDataAccessObject.deleteUserData("Joe");
        authDataAccessObject.deleteAuthData("Joe");

    }

    @Test
    void testLogoutSuccess() throws OtherException {
        assertEquals(1, authDataList.size());
        LogoutRequest request = new LogoutRequest(currentAuthToken, authDataAccessObject);
        UserService.logout(request);
        assertTrue(authDataList.isEmpty());

    }
    @Test
    void testLogoutErrorNullParameter()  {
        LogoutRequest request = new LogoutRequest(null, authDataAccessObject);
        assertThrows(OtherException.class, () -> UserService.logout(request));

    }

    @Test
    void testLogoutErrorInvalidAuthToken()  {
        LogoutRequest logoutRequest = new LogoutRequest("1234", authDataAccessObject);
        assertThrows(UnauthorizedException.class, () -> UserService.logout(logoutRequest));

    }

    @Test
    void testLoginSuccess() throws OtherException {
        LogoutRequest logoutRequest = new LogoutRequest(currentAuthToken, authDataAccessObject);
        UserService.logout(logoutRequest);
        assertTrue(authDataList.isEmpty());
        LoginRequest request = new LoginRequest("Isaac", "Soccer", authDataAccessObject, userDataAccessObject);
        LoginResult result = UserService.login(request);
        currentAuthToken = result.authToken();
        assertEquals(1, authDataList.size());
        assertEquals("Isaac", request.username());
        assertNotNull(result.authToken());
    }

    @Test
    void testLoginErrorNullParameter() throws OtherException {
        RegisterRequest registerRequest = new RegisterRequest("James", "1234", "bob1234", authDataAccessObject, userDataAccessObject);
        RegisterResult registerResult = UserService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken(), authDataAccessObject);
        UserService.logout(logoutRequest);
        LoginRequest loginRequest =  new LoginRequest(null, "1234", authDataAccessObject, userDataAccessObject);
        assertThrows(OtherException.class, () -> UserService.login(loginRequest));
        userDataAccessObject.deleteUserData("James");
    }

    @Test
    void testLoginErrorInvalidPassword() throws OtherException {
        RegisterRequest registerRequest = new RegisterRequest("James", "1234", "bob1234", authDataAccessObject, userDataAccessObject);
        RegisterResult registerResult = UserService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken(), authDataAccessObject);
        UserService.logout(logoutRequest);
        LoginRequest loginRequest =  new LoginRequest("James", "1111", authDataAccessObject, userDataAccessObject);
        assertThrows(UnauthorizedException.class, () -> UserService.login(loginRequest));
    }

    @AfterAll
    static void clear(){
        userDataAccessObject.clearUserData();
        authDataAccessObject.clearAuthdata();

    }



}
