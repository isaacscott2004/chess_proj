package request;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public record JoinGameRequest(String authToken, ChessGame.TeamColor color, Integer gameID, AuthDAO authAccessObject, GameDAO gameAccessObject) {
}
