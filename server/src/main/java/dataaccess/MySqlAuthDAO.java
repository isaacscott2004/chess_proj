package dataaccess;
import model.AuthData;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.UUID;

public class MySqlAuthDAO extends MySqlDAO implements AuthDAO{

    @Override
    public String createAuth(AuthData data) throws DataAccessException {
        String authToken = generateToken();
        data.setAuthToken(authToken);
            String statementOne = "INSERT INTO auth_data (auth_token, username) VALUES (?, ?)";
            executeUpdate(statementOne, data.getAuthToken(), data.getUsername());
            return authToken;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth_data WHERE auth_token=?";
        int affectedRows = executeUpdate(statement, authToken);
        if(affectedRows == 0){
            throw new DataAccessException("There There is no authData with the matching authToken");
        }

    }

    @Override
    public void getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT EXISTS(SELECT 1 FROM auth_data WHERE auth_token=?)";
        boolean authExists = booleanQuery(statement, authToken);
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
        String statement = "TRUNCATE TABLE auth_data";
        executeUpdate(statement);
    }

    // for tests

    @Override
    public Collection<AuthData> getAuthDataStorage() throws DataAccessException {
        ArrayList<AuthData> allData = new ArrayList<>();
        String statement = "SELECT username, auth_token FROM auth_data";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AuthData data = new AuthData();
                    data.setAuthToken(rs.getString("auth_token"));
                    data.setUsername(rs.getString("username"));
                    allData.add(data);
                }
            }
            return allData;
        } catch (SQLException e) {
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {
        String statement = "DELETE FROM auth_data WHERE username=?";
        int affectedRows = executeUpdate(statement, username);
        if(affectedRows == 0) {
            throw new DataAccessException("There There is no authData with the matching username");
        }
    }

    /**
     * generates an authToken
     *
     * @return the authToken
     */
    protected static String generateToken() {
        return UUID.randomUUID().toString();
    }
    private boolean isNameInAuthData(String username) throws DataAccessException {
        String statement = "SELECT EXISTS(SELECT 1 FROM auth_data WHERE username=?)";
        return booleanQuery(statement, username);
    }

}
