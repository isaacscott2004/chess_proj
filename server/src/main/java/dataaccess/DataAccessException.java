package dataaccess;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    final private int statusCode;
    public DataAccessException(String message) {
        this(0, message);

    }
    public DataAccessException( int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }
    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static DataAccessException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return new DataAccessException(status, message);
    }

    public int StatusCode() {
        return statusCode;
    }




//    package exception;
//
//import com.google.gson.Gson;
//
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ResponseException extends Exception {
//    final private int statusCode;
//
//    public ResponseException(int statusCode, String message) {
//        super(message);
//        this.statusCode = statusCode;
//    }
//
//    public String toJson() {
//        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
//    }
//
//    public static ResponseException fromJson(InputStream stream) {
//        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
//        var status = ((Double)map.get("status")).intValue();
//        String message = map.get("message").toString();
//        return new ResponseException(status, message);
//    }
//
//    public int StatusCode() {
//        return statusCode;
//    }
//}
}
