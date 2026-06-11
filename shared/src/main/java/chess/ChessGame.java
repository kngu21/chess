package chess;

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
    private final ChessBoard myBoard;
    private boolean gameIsOver;

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
        gameIsOver = false;
    }

    public void setGameOver(){
        gameIsOver = true;
    }
    public boolean gameOver(){
        return gameIsOver;
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
    public ChessBoard testMove(ChessMove move, ChessBoard board){
        ChessBoard copy = new ChessBoard(board);
        ChessPiece piece = copy.getPiece(move.getStartPosition());
        copy.addPiece(move.getStartPosition(), null);
        copy.addPiece(move.getEndPosition(), piece);
        return copy;
    }

    public boolean checkBite(TeamColor teamColor, ChessBoard board, ChessPosition current, ChessPosition king){
        ChessPiece piece = board.getPiece(current);
        if(piece != null && piece.getTeamColor() != teamColor) {
            Collection<ChessMove> moves = piece.pieceMoves(board, current);
            if(moves != null) {
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(king)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean testIsInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition king = setKing(teamColor, board);
        if(king == null){
            return false;
        }
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition current = new ChessPosition(i,j);
                if(checkBite(teamColor, board, current, king)){
                    return true;
                }
            }
        }
        return false;
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
        if(current == null){
            return null;
        }
        Collection<ChessMove> finals = new ArrayList<>();
            Collection<ChessMove> originals = current.pieceMoves(myBoard, startPosition);
            for (ChessMove move : originals) {
                ChessBoard copy = testMove(move, myBoard);
                if(!testIsInCheck(current.getTeamColor(), copy)) {
                    finals.add(move);
                }
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
        ChessPiece piece = myBoard.getPiece(move.getStartPosition());
        if(piece == null) {
            throw new InvalidMoveException("Piece is null.");
        }
        TeamColor color = piece.getTeamColor();
        if(color != getTeamTurn()){
            throw new InvalidMoveException("Wrong team's turn.");
        }
        if(validMoves(move.getStartPosition()) == null){
            throw new InvalidMoveException("No valid moves.");
        }
        for (ChessMove validMove : validMoves(move.getStartPosition())){
            if(validMove.equals(move)){
                isValid = true;
                break;
            }
        }
        if(!isValid){
            throw new InvalidMoveException("Tried to make invalid move.");
        }
        myBoard.addPiece(move.getStartPosition(), null);
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if(piece.getTeamColor() == TeamColor.WHITE){
                if(move.getEndPosition().getRow() == 8){
                    myBoard.addPiece(move.getEndPosition(), new ChessPiece(color, move.getPromotionPiece()));
                }
                else{
                    myBoard.addPiece(move.getEndPosition(), piece);
                }
            }
            else{
                if(move.getEndPosition().getRow() == 1){
                    myBoard.addPiece(move.getEndPosition(), new ChessPiece(color, move.getPromotionPiece()));
                }
                else{
                    myBoard.addPiece(move.getEndPosition(), piece);
                }
            }
        }
        else{
            myBoard.addPiece(move.getEndPosition(), piece);
        }
        if(color == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
    }

    public ChessPosition setKing(TeamColor color, ChessBoard board){
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition current = new ChessPosition(i,j);
                ChessPiece currentPiece = board.getPiece(current);
                if(currentPiece != null && currentPiece.getTeamColor().equals(color)
                        && currentPiece.getPieceType().equals(ChessPiece.PieceType.KING)){
                    return current;
                }
            }
        }
        throw new IllegalStateException("King not found");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = setKing(teamColor, myBoard);
        if(king == null){
            return false;
        }
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition current = new ChessPosition(i,j);
                if(checkBite(teamColor, myBoard, current, king)){
                    return true;
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

    public boolean checkmateBite(int i, int j, TeamColor teamColor){
        ChessPosition current = new ChessPosition(i,j);
        if(myBoard.getPiece(current) != null && myBoard.getPiece(current).getTeamColor() == teamColor){
            return validMoves(current).isEmpty();
        }
        return true;
    }

    public boolean iterateBoard(TeamColor teamColor){
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                if(!checkmateBite(i, j, teamColor)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return iterateBoard(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return iterateBoard(teamColor);
        }
        return false;
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
