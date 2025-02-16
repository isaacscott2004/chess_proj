package dataaccess;

public class DAOImplmentation {
    public static AuthDAO getAuthDAO(){
        return new MemoryAuthDAO();
    }
    public static GameDAO getGameDAO(){
        return new MemoryGameDAO();
    }
    public static UserDAO getUserDAO(){
        return new MemoryUserDAO();
    }
}

