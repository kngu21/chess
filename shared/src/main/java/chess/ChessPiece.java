package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public void promotePawn(Collection<ChessMove> moves, ChessPosition position, ChessPosition newPosition){
        moves.add(new ChessMove(position, newPosition, PieceType.QUEEN));
        moves.add(new ChessMove(position, newPosition, PieceType.BISHOP));
        moves.add(new ChessMove(position, newPosition, PieceType.ROOK));
        moves.add(new ChessMove(position, newPosition, PieceType.KNIGHT));
    }

    public boolean onBoard(ChessPosition position){
        return (position.getRow() > 0 && position.getRow() < 9  && position.getColumn() > 0  && position.getColumn() < 9);
    }

    public boolean ValidMove(ChessBoard board, ChessPosition newPosition, ChessPiece piece){
        if (onBoard(newPosition)) {
            if (board.getPiece(newPosition) != null) {
                return board.getPiece(newPosition).getTeamColor() != piece.getTeamColor();
            }
            return true;
        }
        return false;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();
        int i = myPosition.getRow();
        int j = myPosition.getColumn();
        if (piece.getPieceType() == PieceType.BISHOP) {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int k = 0; k < 4; k++) {
                while (i < 8 && j < 8 && i > 1 && j > 1) {
                    i+= directions[k][0];
                    j+= directions[k][1];
                    ChessPosition position = new ChessPosition(i, j);
                    if (board.getPiece(position) != null) {
                        if (board.getPiece(position).getTeamColor() != piece.getTeamColor()) {
                            ChessMove move = new ChessMove(myPosition, position, null);
                            moves.add(move);
                        }
                        break;
                    }
                    ChessMove move = new ChessMove(myPosition, position, null);
                    moves.add(move);
                }
                i = myPosition.getRow();
                j = myPosition.getColumn();
            }
        }
        if (piece.getPieceType() == PieceType.KING) {
            int[][] attempts = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
            for (int k = 0; k < 8; k++) {
                ChessPosition position = new ChessPosition(i + attempts[k][0], j + attempts[k][1]);
                if (ValidMove(board, position, piece)) {
                    ChessMove move = new ChessMove(myPosition, position, null);
                    moves.add(move);
                }
            }
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            int[][] attempts = {{2, 1}, {1, 2}, {2, -1}, {-1, 2}, {-2, 1}, {1, -2}, {-1, -2}, {-2, -1}};
            for (int k = 0; k < 8; k++) {
                ChessPosition position = new ChessPosition(i + attempts[k][0], j + attempts[k][1]);
                if (ValidMove(board, position, piece)) {
                    ChessMove move = new ChessMove(myPosition, position, null);
                    moves.add(move);
                }
            }
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                ChessPosition position = new ChessPosition(i + 1, j);
                if (onBoard(position) && board.getPiece(position) == null) {
                    ChessMove move = new ChessMove(myPosition, position, null);
                    if (position.getRow() == 3) {
                        moves.add(move);
                        ChessPosition doubleMove = new ChessPosition(i + 2, j);
                        if (board.getPiece(doubleMove) == null) {
                            ChessMove dubMove = new ChessMove(myPosition, doubleMove, null);
                            moves.add(dubMove);
                        }
                    }
                    else if(position.getRow() == 8){
                        promotePawn(moves, myPosition, position);
                    }
                    else{
                        moves.add(move);
                    }
                }
                ChessPosition leftCapture = new ChessPosition(i + 1, j - 1);
                if (onBoard(leftCapture) && board.getPiece(leftCapture) != null && board.getPiece(leftCapture).getTeamColor() != piece.getTeamColor()) {
                    if(leftCapture.getRow() == 8){
                        promotePawn(moves, myPosition, leftCapture);
                    }
                    else {
                        ChessMove move = new ChessMove(myPosition, leftCapture, null);
                        moves.add(move);
                    }
                }
                ChessPosition rightCapture = new ChessPosition(i + 1, j + 1);
                if (onBoard(rightCapture) && board.getPiece(rightCapture) != null && board.getPiece(rightCapture).getTeamColor() != piece.getTeamColor()) {
                    if(rightCapture.getRow() == 8) {
                        promotePawn(moves, myPosition, rightCapture);
                    }
                    else {
                        ChessMove move = new ChessMove(myPosition, rightCapture, null);
                        moves.add(move);
                    }
                }
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                ChessPosition position = new ChessPosition(i - 1, j);
                if (onBoard(position) && board.getPiece(position) == null) {
                    ChessMove move = new ChessMove(myPosition, position, null);
                    if (position.getRow() == 6) {
                        moves.add(move);
                        ChessPosition doubleMove = new ChessPosition(i - 2, j);
                        if (board.getPiece(doubleMove) == null) {
                            ChessMove dubMove = new ChessMove(myPosition, doubleMove, null);
                            moves.add(dubMove);
                        }
                    }
                    else if(position.getRow() == 1){
                        promotePawn(moves, myPosition, position);
                    }
                    else{
                        moves.add(move);
                    }
                }
                ChessPosition leftCapture = new ChessPosition(i - 1, j + 1);
                if (onBoard(leftCapture) && board.getPiece(leftCapture) != null && board.getPiece(leftCapture).getTeamColor() != piece.getTeamColor()) {
                    if(leftCapture.getRow() == 1){
                        promotePawn(moves, myPosition, leftCapture);
                    }
                    else {
                        ChessMove move = new ChessMove(myPosition, leftCapture, null);
                        moves.add(move);
                    }
                }
                ChessPosition rightCapture = new ChessPosition(i - 1, j - 1);
                if (onBoard(rightCapture) && board.getPiece(rightCapture) != null && board.getPiece(rightCapture).getTeamColor() != piece.getTeamColor()) {
                    if(rightCapture.getRow() == 1) {
                        promotePawn(moves, myPosition, rightCapture);
                    }
                    else {
                        ChessMove move = new ChessMove(myPosition, rightCapture, null);
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }
}
