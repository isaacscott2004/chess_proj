package client;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
            this(0, message);

        }
    public ResponseException( int statusCode, String message){
            super(message);
    }

        public static ResponseException fromJson(InputStream stream) {
            var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
            var status = ((Double)map.get("status")).intValue();
            String message = map.get("message").toString();
            return new ResponseException(status, message);
        }
    }
