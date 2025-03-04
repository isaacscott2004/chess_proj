package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.util.Collection;
import java.util.List;

import java.sql.*;

import static java.sql.Types.NULL;

public class MySqlAuthDAO extends AuthDAO{

    public MySqlAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String createAuth(AuthData data){
        String authToken = AuthDAO.generateToken();
        data.setAuthToken(authToken);
        String statement = "INSERT INTO auth_data (auth_token, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, data.getAuthToken(), data.getUsername());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return authToken;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void getAuth(String authToken) throws DataAccessException {

    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public void clearAuthdata() {

    }

    @Override
    public Collection<AuthData> getAuthDataStorage() throws DataAccessException {
        return List.of();
    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {

    }

    private void executeUpdate(String statement, Object... params) throws DataBaseException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                for(int i = 0; i < params.length; i++){
                    var param = params[i];
                    if(param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

            }
    } catch (SQLException e){
            throw new DataBaseException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
    """
    CREATE TABLE IF NOT EXISTS auth_data (
    `auth_token` varchar(256) DEFAULT NULL,
    `username` varchar(256) NOT NULL,
    PRIMARY KEY (`username`),
    INDEX(auth_token)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };


    private void configureDatabase() throws DataBaseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataBaseException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }
}
