package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        this(0, message);

    }
    public DataAccessException( int statusCode, String message){
        super(message);
    }

}
