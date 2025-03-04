package service;


import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.AfterAll;
import request.LoginRequest;
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
    static void setUp() throws Exception {
        userDataAccessObject = new MemoryUserDAO();
        authDataAccessObject = new MemoryAuthDAO();
        authDataList = authDataAccessObject.getAuthDataStorage();
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "Soccer", "isaacscottirwin@gmail.com");
        RegisterResult registerResult = UserService.register(registerRequest, authDataAccessObject, userDataAccessObject);
        currentAuthToken = registerResult.authToken();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("Bob", "1234", "bob1234");
        RegisterResult registerResult = UserService.register(request, authDataAccessObject, userDataAccessObject);
        assertEquals("Bob", registerResult.username());
        assertNotNull(registerResult.authToken());
        assertFalse(registerResult.authToken().isEmpty());
        userDataAccessObject.deleteUserData("Bob");
        authDataAccessObject.deleteAuthData("Bob");
    }

    @Test
    void testRegisterErrorNullParameter() {
        RegisterRequest request = new RegisterRequest("Billy", "1234", null);
        assertThrows(BadRequestException.class, () -> UserService.register(request, authDataAccessObject, userDataAccessObject));
    }

    @Test
    void testRegisterErrorSameUsername() throws Exception {
        RegisterRequest requestOne = new RegisterRequest("Joe", "1234", "bob1234");
        RegisterRequest requestTwo = new RegisterRequest("Joe", "1235", "bo1234");
        UserService.register(requestOne, authDataAccessObject, userDataAccessObject);
        assertThrows(AlreadyTakenException.class, () -> UserService.register(requestTwo, authDataAccessObject, userDataAccessObject));
        userDataAccessObject.deleteUserData("Joe");
        authDataAccessObject.deleteAuthData("Joe");

    }

    @Test
    void testLogoutSuccess() {
        assertEquals(1, authDataList.size());
        UserService.logout(currentAuthToken, authDataAccessObject);
        assertTrue(authDataList.isEmpty());

    }

    @Test
    void testLogoutErrorInvalidAuthToken() {
        assertThrows(UnauthorizedException.class, () -> UserService.logout("1234", authDataAccessObject));

    }

    @Test
    void testLoginSuccess() throws Exception {
        UserService.logout(currentAuthToken, authDataAccessObject);
        assertTrue(authDataList.isEmpty());
        LoginRequest request = new LoginRequest("Isaac", "Soccer");
        LoginResult result = UserService.login(request, authDataAccessObject, userDataAccessObject);
        currentAuthToken = result.authToken();
        assertEquals(1, authDataList.size());
        assertEquals("Isaac", request.username());
        assertNotNull(result.authToken());
    }

    @Test
    void testLoginErrorNullParameter() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("James", "1234", "bob1234");
        RegisterResult registerResult = UserService.register(registerRequest, authDataAccessObject, userDataAccessObject);
        UserService.logout(registerResult.authToken(), authDataAccessObject);
        LoginRequest loginRequest = new LoginRequest(null, "1234");
        assertThrows(BadRequestException.class, () -> UserService.login(loginRequest, authDataAccessObject, userDataAccessObject));
        userDataAccessObject.deleteUserData("James");
    }

    @Test
    void testLoginErrorInvalidPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("James", "1234", "bob1234");
        RegisterResult registerResult = UserService.register(registerRequest, authDataAccessObject, userDataAccessObject);
        UserService.logout(registerResult.authToken(), authDataAccessObject);
        LoginRequest loginRequest = new LoginRequest("James", "1111");
        assertThrows(UnauthorizedException.class, () -> UserService.login(loginRequest, authDataAccessObject, userDataAccessObject));
    }

    @AfterAll
    static void clear() throws DataAccessException {
        userDataAccessObject.clearUserData();
        authDataAccessObject.clearAuthdata();

    }


}
