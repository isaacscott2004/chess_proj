package client;

import dataaccess.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.ClearService;


public class ServerFacadeTests {

    private static Server server;
    private static GameDAO gameDataAccessObject;
    private static UserDAO userDataAccessObject;
    private static AuthDAO authDataAccessObject;

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        gameDataAccessObject = new MySqlGameDAO();
        userDataAccessObject = new MySqlUserDAO();
        authDataAccessObject = new MySqlAuthDAO();
    }
    @BeforeEach
    public void setup() throws DataAccessException {
        ClearService.clear(authDataAccessObject, userDataAccessObject, gameDataAccessObject);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
