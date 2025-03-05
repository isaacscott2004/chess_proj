package dataaccess;
import model.AuthData;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import static java.sql.Types.NULL;

public class MySqlAuthDAO extends AuthDAO{

    public MySqlAuthDAO() {
            configureDatabase();
    }
    @Override
    public String createAuth(AuthData data) throws DataAccessException {
        String authToken = AuthDAO.generateToken();
        data.setAuthToken(authToken);
        String statement = "INSERT INTO auth_data (auth_token, username) VALUES (?, ?)";
        executeUpdate(statement, data.getAuthToken(), data.getUsername());
        return authToken;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth_data WHERE auth_token=?";
        int affectedRows = executeUpdate(statement);
        if(affectedRows == 0){
            throw new DataAccessException("There There is no authData with the matching authToken");
        }

    }

    @Override
    public void getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT EXISTS(SELECT 1 FROM auth_data WHERE auth_token=?)";
        boolean authExists = executeQuery(statement);
        if(!(authExists)){
            throw new DataAccessException("There There is no authData with the matching authToken");
        }

    }

    @Override
    public String getUsername(String authToken) throws DataAccessException, DataBaseException {
        String statement = "SELECT username FROM auth_data WHERE auth_token=?";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getString("username");
                } else{
                    throw new DataAccessException("There is no username with the matching authToken");
                }
            }
        } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void clearAuthdata() throws DataAccessException {
        String statement = "DELETE FROM auth_data";
//        String statement = "DROP TABLE auth_data";
//        String statement = "TRUNCATE TABLE auth_data";
        executeUpdate(statement);

    }

    // for tests

    @Override
    public Collection<AuthData> getAuthDataStorage() throws DataAccessException {
        ArrayList<AuthData> allData = new ArrayList<>();
        String statement = "SELECT username, auth_token FROM auth_data";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    AuthData data = new AuthData();
                    data.setAuthToken(rs.getString("username"));
                    data.setAuthToken(rs.getString("auth_token"));
                    allData.add(data);
                } else{
                    throw new DataAccessException("There is no username with the matching authToken");
                }
            }
        } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statement, e.getMessage()));
        }
        return allData;
    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {
        String statement = "DELETE FROM auth_data WHERE username=?";
        int affectedRows = executeUpdate(statement);
        if(affectedRows == 0) {
            throw new DataAccessException("There There is no authData with the matching username");
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataBaseException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                for(int i = 0; i < params.length; i++){
                    var param = params[i];
                    if(param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                 return ps.executeUpdate();

            }
    } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute update: %s, %s", statement, e.getMessage()));
        }
    }

    private boolean executeQuery(String statement, Object... params) throws DataBaseException, DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                for(int i = 0; i < params.length; i++){
                    var param = params[i];
                    if(param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
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


    private void configureDatabase() throws DataBaseException {
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
}
