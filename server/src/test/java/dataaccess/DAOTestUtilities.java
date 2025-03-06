package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

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
}
