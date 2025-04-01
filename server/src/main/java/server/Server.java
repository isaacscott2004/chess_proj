package server;

import dataaccess.*;
import model.GameData;
import server.websocket.WebSocketHandler;
import spark.*;

public class Server {
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private final WebSocketHandler webSocketHandler;

    public Server(){
        chooseMemoryType(MemoryType.SQL_MEMORY);
        this.webSocketHandler = new WebSocketHandler(authDAO, gameDAO);
    }




    public int run(int desiredPort)  {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", webSocketHandler);
        Spark.delete("/db", new ClearHandler(authDAO, userDAO, gameDAO));
        Spark.post("/user", new RegisterHandler(authDAO, userDAO));
        Spark.post("/session", new LoginHandler(authDAO, userDAO));
        Spark.delete("/session", new LogoutHandler(authDAO));
        Spark.post("/game", new CreateGameHandler(authDAO, gameDAO));
        Spark.put("/game", new JoinGameHandler(authDAO, gameDAO));
        Spark.get("/game", new ListGamesHandler(authDAO, gameDAO));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    public enum MemoryType {
        IN_MEMORY,
        SQL_MEMORY
    }
    private  void chooseMemoryType(MemoryType memoryType){
        if(memoryType == MemoryType.IN_MEMORY){
            this.authDAO = new MemoryAuthDAO();
            this.userDAO = new MemoryUserDAO();
            this.gameDAO = new MemoryGameDAO();
        }
        else if(memoryType == MemoryType.SQL_MEMORY) {
            new MySqlDAO();
            this.authDAO = new MySqlAuthDAO();
            this.userDAO = new MySqlUserDAO();
            this.gameDAO = new MySqlGameDAO();
        }
    }
}
