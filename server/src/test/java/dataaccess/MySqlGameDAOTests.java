package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class MySqlGameDAOTests {
    private static GameDAO gameDAOAccessObject;
    private static Server.MemoryType memoryType;
    private static String[] dropStatements;

    @BeforeAll
    static void setupOnce(){
        memoryType = Server.MemoryType.SQL_MEMORY;
        HashMap<String, Object> daos = DAOTestUtilities.chooseMemoryType(memoryType);
        gameDAOAccessObject = (GameDAO) daos.get("game");
        dropStatements = DAOTestUtilities.dropStatements;


    }
    @BeforeEach
    void setup() throws DataAccessException {
        if(memoryType == Server.MemoryType.SQL_MEMORY) {
            DAOTestUtilities.dropTables(dropStatements);
            new MySqlDAO();

        }else{
            gameDAOAccessObject.clearGameData();
        }
        GameData.resetGameIDCounter();
        gameDAOAccessObject.createGame("First Game");
        gameDAOAccessObject.createGame("Second Game");
        gameDAOAccessObject.createGame("Third Game");
    }

    @Test
    void testGetListGames() throws DataAccessException {
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDAOAccessObject.getListGames());
        assertEquals(3, listOfGames.size());
        ArrayList<String> expectedNames = new ArrayList<>();
        expectedNames.add("First Game");
        expectedNames.add("Second Game");
        expectedNames.add("Third Game");
        for(GameData gameData : listOfGames){
            assertTrue(expectedNames.contains(gameData.getGameName()));
        }
    }

    @Test
    void testCreateGameSuccess() throws DataAccessException {
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDAOAccessObject.getListGames());
        assertEquals(3, listOfGames.size());
        gameDAOAccessObject.createGame("Test game");
        listOfGames =  new ArrayList<>(gameDAOAccessObject.getListGames());
        assertEquals(4, listOfGames.size());
        GameData lastAdded = listOfGames.getLast();
        assertEquals("Test game", lastAdded.getGameName());
    }

    @Test
    void testCreateGameNullValue() throws DataAccessException {
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDAOAccessObject.getListGames());
        assertEquals(3, listOfGames.size());
        assertThrows(DataAccessException.class, () ->
                gameDAOAccessObject.createGame(null));
        assertEquals(3, listOfGames.size());
    }

    @Test
    void testCheckGameIDSuccess() {
        assertDoesNotThrow(() ->
                gameDAOAccessObject.checkGameID(1));
    }

    @Test
    void testCheckGameIDFailure(){
        assertThrows(DataAccessException.class, () ->
                gameDAOAccessObject.checkGameID(100));
    }

    @Test
    void testUpdateGameSuccess() throws DataAccessException {
        gameDAOAccessObject.updateGame("Isaac", ChessGame.TeamColor.WHITE, 1);
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDAOAccessObject.getListGames());
        GameData firstGame = listOfGames.getFirst();
        assertEquals("Isaac", firstGame.getWhiteUsername());

    }

    @Test
    void testUpdateGameFailure() throws DataAccessException {
        gameDAOAccessObject.updateGame("Isaac", ChessGame.TeamColor.WHITE, 1);
        assertThrows(DataAccessException.class, () ->
                gameDAOAccessObject.updateGame("Messi", ChessGame.TeamColor.WHITE, 1));
    }

    @Test
    void testClearGameData() throws DataAccessException {
        ArrayList<GameData> listOfGames = new ArrayList<>(gameDAOAccessObject.getListGames());
        assertEquals(3, listOfGames.size());
        gameDAOAccessObject.clearGameData();
        listOfGames = new ArrayList<>(gameDAOAccessObject.getListGames());
        assertTrue(listOfGames.isEmpty());

    }
    @Test
    void testGetLargestGameId() throws DataAccessException {
        assertEquals(3, gameDAOAccessObject.getLargestGameID());
    }

}
