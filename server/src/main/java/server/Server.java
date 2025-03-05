package server;

import dataaccess.*;
import spark.*;

public class Server {
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    public Server(){
        chooseMemoryType(false);
    }



    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", new ClearHandler(authDAO, userDAO, gameDAO));
        Spark.post("/user", new RegisterHandler(authDAO, userDAO));
        Spark.post("/session", new LoginHandler(authDAO, userDAO));
        Spark.delete("/session", new LogoutHandler(authDAO));
        Spark.post("/game", new CreateGameHandler(authDAO, gameDAO));
        Spark.put("/game", new JoinGameHandler(authDAO, gameDAO));
        Spark.get("/game", new ListGamesHandler(authDAO, gameDAO));

        //This line initializes the server and can be removed once you have a functioning endpoint

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private  void chooseMemoryType(boolean inMemory){
        if(inMemory){
            this.authDAO = new MemoryAuthDAO();
            this.userDAO = new MemoryUserDAO();
            this.gameDAO = new MemoryGameDAO();
        }
        else {
            new MySqlDAO();
            this.authDAO = new MySqlAuthDAO();
            this.userDAO = new MySqlUserDAO();
            this.gameDAO = new MySqlGameDAO();
        }
    }
}
