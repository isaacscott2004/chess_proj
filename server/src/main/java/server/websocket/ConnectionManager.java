package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, Set<Session>> sessionMap;

    public ConnectionManager(){
        this.sessionMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, Set<Session>> getSessionMap(){
        return this.sessionMap;
    }

    public void addSessionToGame(int gameID, Session session){
        this.sessionMap.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        this.sessionMap.get(gameID).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session) throws WebSocketException{
        Set<Session> specifiedGame = this.sessionMap.get(gameID);
        if(specifiedGame == null){
            throw new WebSocketException("Game ID: " + gameID + " does not exist.");
        }
        if(!(specifiedGame.contains(session))){
            throw new WebSocketException("Session: " + session + " does not exist within game " + gameID + ".");
        }
        specifiedGame.remove(session);
    }

    public void removeSession(Session session){
        boolean removed = false;
        for(Set<Session> sessions : sessionMap.values()){
            if(sessions.remove(session)){
                removed = true;
            }
        }
        if(!(removed)){
            throw new WebSocketException("Session: " + session + " does not exist.");
        }
    }

    public Set<Session> getSessionsFromGame(int gameID){
        return this.sessionMap.get(gameID);
    }


}
