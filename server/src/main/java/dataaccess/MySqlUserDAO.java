package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlUserDAO extends MySqlDAO implements UserDAO {

    @Override
    public void addUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user_data (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = storeUserPassword(user.getPassword());
        executeUpdate(statement, user.getUsername(), hashedPassword, user.getEmail());

    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        String hashedPassword = getHashPassword(username);
        Boolean passwordsMatch = BCrypt.checkpw(password, hashedPassword);
        Boolean usernamesMatch = containsUsername(username);
        return passwordsMatch && usernamesMatch;
    }

    @Override
    public void clearUserData() throws DataAccessException {
        String statement = "TRUNCATE TABLE user_data";
        executeUpdate(statement);
    }

    @Override
    public boolean containsUsername(String username) throws DataAccessException {
        String statement = "SELECT EXISTS(SELECT 1 FROM user_data WHERE username=?)";
        return booleanQuery(statement, username);
    }

    @Override
    public Collection<UserData> getUserDataStorage() throws DataAccessException {
        ArrayList<UserData> allData = new ArrayList<>();
        String statement = "SELECT username, password, email FROM user_data";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    UserData data = new UserData();
                    data.setUsername(rs.getString("username"));
                    data.setPassword(rs.getString("password"));
                    data.setEmail(rs.getString("email"));
                    allData.add(data);
                }
            }
        } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statement, e.getMessage()));
        }
        return allData;

    }

    @Override
    public void deleteUserData(String username) throws DataAccessException {
        String statement = "DELETE FROM user_data WHERE username=?";
        int affectedRows = executeUpdate(statement, username);
        if(affectedRows == 0) {
            throw new DataAccessException("There There is no userData with the matching username");
        }
    }



    private String storeUserPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

    }

    private String getHashPassword(String username) throws DataAccessException, DataBaseException{
        String statement = "SELECT password FROM user_data WHERE username=?";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getString("password");
                } else{
                    throw new DataAccessException("There is no username with the matching authToken");
                }
            }
        } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statement, e.getMessage()));
        }


    }
}

