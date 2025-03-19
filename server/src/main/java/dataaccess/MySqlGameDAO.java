package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO extends MySqlDAO implements GameDAO{

    @Override
    public Collection<GameData> getListGames() throws DataAccessException {
        ArrayList<GameData> allData = new ArrayList<>();
        String statement = "SELECT gameID, white_username, black_username, game_name, json_game FROM game_data";
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    GameData data = new GameData();
                    data.setGameID(rs.getInt("gameID"));
                    data.setWhiteUsername(rs.getString("white_username"));
                    data.setBlackUsername(rs.getString("black_username"));
                    data.setGameName(rs.getString("game_name"));
                    data.setGame(readJsonGame(rs.getString("json_game")));
                    allData.add(data);
                }
            }
        } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statement, e.getMessage()));
        }
        return allData;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int id = getLargestGameID();
        id++;
        GameData data = new GameData(id, null, null, gameName, new ChessGame());
        String statement = "INSERT INTO game_data(gameID, white_username, black_username, game_name, json_game) VALUES " +
                "(?, ?, ?, ?, ?)";
        executeUpdate(statement, id, data.getWhiteUsername(), data.getBlackUsername(), data.getGameName(),
                setJsonGame(data.getGame()));
        return data.getGameID();
    }

    @Override
    public void checkGameID(int gameID) throws DataAccessException {
        String statement = "SELECT gameID FROM game_data WHERE gameID=?";
        boolean idExists = booleanQuery(statement, gameID);
        if(!(idExists)){
            throw new DataAccessException("There is no game with the specified gameID");
        }
    }

    @Override
    public int getLargestGameID() throws DataAccessException {
        String statement = "SELECT MAX(gameID) AS maxGameID FROM game_data";
        Integer maxGameID = null;

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    maxGameID = rs.getInt("maxGameID");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to execute query: " + e.getMessage());
        }
        if(maxGameID == null){
            return 1;
        }
        return maxGameID;
    }

    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        String statementOne = "SELECT gameID, white_username, black_username, game_name, json_game FROM game_data WHERE " +
                "gameID=?";
        GameData selectedData = null;
        try(Connection connOne = DatabaseManager.getConnection()){
            try(PreparedStatement psOne = connOne.prepareStatement(statementOne)){
                psOne.setInt(1, gameID);
                ResultSet rsOne = psOne.executeQuery();
                if (rsOne.next()){
                    GameData data  = new GameData();
                    data.setGameID(rsOne.getInt("gameID"));
                    data.setWhiteUsername(rsOne.getString("white_username"));
                    data.setBlackUsername(rsOne.getString("black_username"));
                    data.setGameName(rsOne.getString("game_name"));
                    data.setGame(readJsonGame(rsOne.getString("json_game")));
                    selectedData = data;
                }
            }
        } catch (SQLException e){
            throw new DataBaseException(String.format("unable to execute query: %s, %s", statementOne, e.getMessage()));
        }
        if(selectedData == null){
            throw new DataAccessException("There is no game with the specified gameID");
        }
        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            if (selectedData.getWhiteUsername() != null) {
                throw new DataAccessException("White username already taken");
            }
            selectedData.setWhiteUsername(username);
        } else {
            if (selectedData.getBlackUsername() != null) {
                throw new DataAccessException("Black username already taken");
            }
            selectedData.setBlackUsername(username);
        }
        String statementTwo = "UPDATE game_data SET white_username=?, black_username=? WHERE gameID=?";
        executeUpdate(statementTwo, selectedData.getWhiteUsername(), selectedData.getBlackUsername(), selectedData.getGameID());
    }

    @Override
    public void clearGameData() throws DataAccessException {
        String statement = "TRUNCATE TABLE game_data";
        executeUpdate(statement);
    }

    private ChessGame readJsonGame(String jsonGame){
        return new Gson().fromJson(jsonGame, ChessGame.class);
    }
    private String setJsonGame(ChessGame game){
        return  new Gson().toJson(game);
    }
}
