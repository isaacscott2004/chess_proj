package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class MySqlDAO {

    public MySqlDAO(){
        configureDatabase();
    }


    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                for(int i = 0; i < params.length; i++){
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                return ps.executeUpdate();

            }
        } catch (SQLException e){
            throw new DataAccessException(String.format("unable to execute update: %s, %s", statement, e.getMessage()));
        }
    }

    protected boolean booleanQuery(String statement, Object... params) throws DataBaseException, DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                for(int i = 0; i < params.length; i++){
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statement, e.getMessage()));
        }
        return false;
    }

    protected  void configureDatabase() throws DataBaseException {
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataBaseException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

    private final String[] createStatements = {
"""
CREATE TABLE IF NOT EXISTS auth_data (
    `auth_token` varchar(256) UNIQUE,
    `username` varchar(256) NOT NULL,
    INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
""",
"""
CREATE TABLE IF NOT EXISTS user_data (
    `username` varchar(256) NOT NULL,
    `password` varchar(256) NOT NULL,
    `email` varchar(256) NOT NULL,
    PRIMARY KEY (`username`),
    INDEX(password),
    INDEX(email)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
""",
"""
CREATE TABLE IF NOT EXISTS game_data (
    `gameID` int  NOT NULL,
    `white_username` varchar(256) DEFAULT NULL,
    `black_username` varchar(256) DEFAULT NULL,
    `game_name` varchar(256) NOT NULL,
    `json_game` TEXT NOT NULL,
    PRIMARY KEY (`gameID`),
    INDEX(white_username),
    INDEX(black_username),
    INDEX(game_name),
    INDEX(json_game(100))
    )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
"""
    };
}
