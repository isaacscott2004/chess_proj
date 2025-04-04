package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, Set<Connection>> connectionMap;
    private final ConcurrentHashMap<Session, Connection> sessionConnectionMap;

    public ConnectionManager(){
        this.connectionMap = new ConcurrentHashMap<>();
        this.sessionConnectionMap = new ConcurrentHashMap<>();

    }

    public void addSessionToGame(int gameID, Connection connection){
        this.connectionMap.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        this.connectionMap.get(gameID).add(connection);
        this.sessionConnectionMap.put(connection.getSession(), connection);
    }

    public void removeSessionFromGame(int gameID, Connection connection) throws WebSocketException{
        String thisAuthToken = connection.getAuthToken();
        Set<Connection> specifiedGame = this.connectionMap.get(gameID);

        if(specifiedGame == null){
            throw new WebSocketException("Game ID: " + gameID + " does not exist.");
        }

        Connection removed = null;
        for(Connection conn : specifiedGame){
            if(conn.getAuthToken().equals(thisAuthToken)){
                removed = conn;
                break;
            }
        }

        if (removed == null) {
            throw new WebSocketException("Connection with authToken: " + thisAuthToken + " does not exist within game " + gameID + ".");
        }

        specifiedGame.remove(removed);
        int size = specifiedGame.size();
        System.out.println(size);
    }

    public void removeSession(Session session){
        Connection connection = this.sessionConnectionMap.remove(session);
        if(connection != null) {
            for (Set<Connection> connections : connectionMap.values()) {
                connections.remove(connection);
            }
        }
    }

    public Set<Connection> getSessionsFromGame(int gameID){
        return this.connectionMap.get(gameID);
    }


}
