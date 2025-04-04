package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }
    public Session getSession(){
        return this.session;
    }
    public String getAuthToken(){
        return this.authToken;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "authToken='" + authToken + '\'' +
                ", session=" + session +
                '}';
    }

}
