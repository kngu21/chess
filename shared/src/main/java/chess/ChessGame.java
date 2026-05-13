package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard myBoard;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(myBoard, chessGame.myBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, myBoard);
    }

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.myBoard = new ChessBoard();
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece current = myBoard.getPiece(startPosition);
        Collection<ChessMove> finals = new ArrayList<>();
        if(current != null) {
            Collection<ChessMove> originals = current.pieceMoves(myBoard, startPosition);
            for (ChessMove move : originals) {
                finals.add(move);
            }
        }
        if(finals.isEmpty()){
            return null;
        }
        return finals;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean isValid = false;
        ChessBoard tester = getBoard();
        ChessPiece piece = tester.getPiece(move.getStartPosition());
        TeamColor color = piece.getTeamColor();
        if(color != getTeamTurn()){
            throw new InvalidMoveException("Wrong team's turn.");
        }
        for ( ChessMove validMove : validMoves(move.getStartPosition())){
            if(validMove.equals(move)){
                isValid = true;
                break;
            }
        }
        if(!isValid){
            throw new InvalidMoveException("Tried to make invalid move.");
        }
        myBoard.addPiece(move.getStartPosition(), null);
        myBoard.addPiece(move.getEndPosition(), piece);
        if(color == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
    }

    public ChessPosition setKing(TeamColor color){
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition current = new ChessPosition(i,j);
                if(myBoard.getPiece(current) != null && myBoard.getPiece(current).getTeamColor() == color && myBoard.getPiece(current).getPieceType() == ChessPiece.PieceType.KING){
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = setKing(teamColor);
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition current = new ChessPosition(i,j);
                ChessPiece piece = myBoard.getPiece(current);
                if(piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(myBoard, current);
                    if(moves != null) {
                        for (ChessMove move : moves) {
                            if (move.getEndPosition().equals(king)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition current = new ChessPosition(i,j);
                ChessPiece piece = myBoard.getPiece(current);
                if(piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(myBoard, current);
                    if(moves != null) {
                        for (ChessMove move : moves) {
                            if(!isInCheck(teamColor)){
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition current = new ChessPosition(i,j);
                myBoard.addPiece(current, board.getPiece(current));
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }
}
