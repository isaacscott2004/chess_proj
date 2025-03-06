package dataaccess;

import server.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class DAOTestUtilities {

    protected static void dropTables(String[] dropStatements) throws DataBaseException{
        try {
            try(Connection conn = DatabaseManager.getConnection()) {
                for (var statement : dropStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }

            }
        } catch (SQLException | DataAccessException e) {
            throw new DataBaseException(String.format("Unable to drop tables: %s", e.getMessage()));        }
    }


    private static AuthDAO chooseAuthMemoryType(Server.MemoryType memoryType){
        AuthDAO authAccessObject;
        if(memoryType == Server.MemoryType.IN_MEMORY){
            authAccessObject = new MemoryAuthDAO();

        }
        else{
            authAccessObject = new MySqlAuthDAO();

        }
        return authAccessObject;

    }
    private static UserDAO chooseUserMemoryType(Server.MemoryType memoryType){
        UserDAO userAccessObject;
        if(memoryType == Server.MemoryType.IN_MEMORY){
            userAccessObject = new MemoryUserDAO();

        }
        else{
            userAccessObject = new MySqlUserDAO();

        }
        return userAccessObject;

    }
    private static GameDAO chooseGameMemoryType(Server.MemoryType memoryType){
        GameDAO gameAccessObject;
        if(memoryType == Server.MemoryType.IN_MEMORY){
            gameAccessObject = new MemoryGameDAO();

        }
        else{
            gameAccessObject = new MySqlGameDAO();

        }
        return gameAccessObject;

    }
    protected static HashMap<String, Object> chooseMemoryType(Server.MemoryType memoryType) {
        HashMap<String, Object> daos = new HashMap<>();
        daos.put("auth", chooseAuthMemoryType(memoryType));
        daos.put("user", chooseUserMemoryType(memoryType));
        daos.put("game", chooseGameMemoryType(memoryType));
        return daos;
    }
    protected static String[] dropStatements = new String[]{
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
