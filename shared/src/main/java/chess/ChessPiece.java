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
    public boolean ValidMove(ChessBoard board, ChessPosition newPosition, ChessPiece piece){
        if (newPosition.getRow() > 0 && newPosition.getRow() < 9 && newPosition.getColumn() > 0 && newPosition.getColumn() < 9) {
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
            while(i < 8 && j < 8) {
                ChessPosition position = new ChessPosition(i + 1, j + 1);
                if (board.getPiece(position) != null){
                    if(board.getPiece(position).getTeamColor() != piece.getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, position, null);
                        moves.add(move);
                    }
                    break;
                }
                ChessMove move = new ChessMove(myPosition, position, null);
                moves.add(move);
                i++;
                j++;
            }
            i = myPosition.getRow();
            j = myPosition.getColumn();
            while(i < 8 && j > 1) {
                ChessPosition position = new ChessPosition(i + 1, j - 1);
                if (board.getPiece(position) != null){
                    if(board.getPiece(position).getTeamColor() != piece.getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, position, null);
                        moves.add(move);
                    }
                    break;
                }
                ChessMove move = new ChessMove(myPosition, position, null);
                moves.add(move);
                i++;
                j--;
            }
            i = myPosition.getRow();
            j = myPosition.getColumn();
            while(i > 1 && j < 8) {
                ChessPosition position = new ChessPosition(i - 1, j + 1);
                if (board.getPiece(position) != null){
                    if(board.getPiece(position).getTeamColor() != piece.getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, position, null);
                        moves.add(move);
                    }
                    break;
                }
                ChessMove move = new ChessMove(myPosition, position, null);
                moves.add(move);
                i--;
                j++;
            }
            i = myPosition.getRow();
            j = myPosition.getColumn();
            while(i > 1 && j > 1) {
                ChessPosition position = new ChessPosition(i - 1, j - 1);
                if (board.getPiece(position) != null){
                    if(board.getPiece(position).getTeamColor() != piece.getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, position, null);
                        moves.add(move);
                    }
                    break;
                }
                ChessMove move = new ChessMove(myPosition, position, null);
                moves.add(move);
                i--;
                j--;
            }
        }
        if (piece.getPieceType() == PieceType.KING){
            int[][] attempts = {{1,0},{1,1}, {0,1}, {-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
            for(int k = 0; k < 8; k++) {
                ChessPosition position = new ChessPosition(i + attempts[k][0], j + attempts[k][1]);
                if (ValidMove(board, position, piece)) {
                    ChessMove move = new ChessMove(myPosition, position, null);
                    moves.add(move);
                }
            }
        }
        if (piece.getPieceType() == PieceType.KNIGHT){
            int[][] attempts = {{2,1},{1,2}, {2,-1}, {-1,2},{-2,1},{1,-2},{-1,-2},{-2,-1}};
            for(int k = 0; k < 8; k++) {
                ChessPosition position = new ChessPosition(i + attempts[k][0], j + attempts[k][1]);
                if (ValidMove(board, position, piece)) {
                    ChessMove move = new ChessMove(myPosition, position, null);
                    moves.add(move);
                }
            }
        }
        return moves;
    }
}
