package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static boolean onBoard(ChessPosition position){
        return position.getRow() > 0 && position.getRow() < 9 && position.getColumn() > 0 && position.getColumn() < 9;
    }
    public boolean validMove(ChessBoard board, ChessPosition newPosition, ChessPiece piece){
        if(onBoard(newPosition)){
            if(board.getPiece(newPosition) != null){
                return board.getPiece(newPosition).getTeamColor() != piece.getTeamColor();
            }
            return true;
        }
        return false;
    }
    public void promotePawn(Collection<ChessMove> moves, ChessPosition current, ChessPosition newbie){
        moves.add(new ChessMove(current, newbie, PieceType.QUEEN));
        moves.add(new ChessMove(current, newbie, PieceType.ROOK));
        moves.add(new ChessMove(current, newbie, PieceType.BISHOP));
        moves.add(new ChessMove(current, newbie, PieceType.KNIGHT));
    }

    public void sliders(ChessBoard board, ChessPosition myPosition, int [][] directions, int i, int j, ChessPiece piece, Collection<ChessMove> moves){
        for (int[] direction : directions) {
            while (true) {
                i += direction[0];
                j += direction[1];
                if (!validMove(board, new ChessPosition(i, j), piece)) {
                    break;
                }
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                if (board.getPiece(new ChessPosition(i, j)) != null) {
                    break;
                }
            }
            i = myPosition.getRow();
            j = myPosition.getColumn();
        }
    }

    public void theKs(int i, int j, int [][] directions, ChessBoard board, ChessPiece piece,
                      Collection<ChessMove> moves, ChessPosition myPosition){
        for(int k = 0; k < 8; k++){
            ChessPosition newbie = new ChessPosition(i+directions[k][0], j+directions[k][1]);
            if(validMove(board, newbie, piece)){
                moves.add(new ChessMove(myPosition, newbie, null));
            }
        }
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int i = myPosition.getRow();
        int j = myPosition.getColumn();
        ChessPiece piece = new ChessPiece(pieceColor, type);
        if(type == PieceType.BISHOP){
            int [][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}};
            sliders(board,myPosition,directions,i,j,piece,moves);
        }
        if(type == PieceType.ROOK){
            int [][] directions = {{1,0},{0,-1},{-1,0},{0,1}};
            sliders(board,myPosition,directions,i,j,piece,moves);
        }
        if(type == PieceType.QUEEN){
            int [][] directions = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{0,-1},{-1,0},{0,1}};
            sliders(board,myPosition,directions,i,j,piece,moves);
        }
        if(type == PieceType.KING){
            int [][] directions = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{0,-1},{-1,0},{0,1}};
            theKs(i, j, directions, board, piece, moves, myPosition);
        }
        if(type == PieceType.KNIGHT){
            int [][] directions = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{-1,-2},{1,-2}};
            theKs(i, j, directions, board, piece, moves, myPosition);
        }
        if(type == PieceType.PAWN){
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                ChessPosition oneAhead = new ChessPosition(i+1, j);
                aheadMoves(board,oneAhead,moves,i,j, 1, 3, 8, myPosition);
                ChessPosition leftCapture = new ChessPosition(i+1, j-1);
                captureMoves(board, leftCapture, myPosition, piece, moves, i,1, 8);
                ChessPosition rightCapture = new ChessPosition(i+1, j+1);
                captureMoves(board, rightCapture, myPosition, piece, moves, i,1, 8);
            }
            else{
                ChessPosition oneAhead = new ChessPosition(i-1, j);
                aheadMoves(board,oneAhead,moves,i,j, -1, 6, 1, myPosition);
                ChessPosition leftCapture = new ChessPosition(i-1, j+1);
                captureMoves(board, leftCapture, myPosition, piece, moves, i,-1, 1);
                ChessPosition rightCapture = new ChessPosition(i-1, j-1);
                captureMoves(board, rightCapture, myPosition, piece, moves, i,-1, 1);
            }
        }
        return moves;
    }

    public void aheadMoves(ChessBoard board, ChessPosition oneAhead, Collection<ChessMove> moves,
                           int i, int j, int k, int l, int m, ChessPosition myPosition){
        if(board.getPiece(oneAhead) == null){
            if(i+k == l){
                moves.add(new ChessMove(myPosition, oneAhead, null));
                ChessPosition twoAhead = new ChessPosition(i+(2*k), j);
                if(board.getPiece(twoAhead) == null){
                    moves.add(new ChessMove(myPosition, twoAhead, null));
                }
            }
            else if( i+k == m){
                promotePawn(moves, myPosition, oneAhead);
            }
            else{
                moves.add(new ChessMove(myPosition, oneAhead, null));
            }
        }
    }

    public void captureMoves(ChessBoard board, ChessPosition capture, ChessPosition myPosition,
                             ChessPiece piece, Collection<ChessMove> moves, int i, int k, int l){
        if(onBoard(capture)) {
            ChessPiece currentPiece = board.getPiece(capture);
            if (currentPiece != null && currentPiece.getTeamColor() != piece.getTeamColor()) {
                if (i + k == l) {
                    promotePawn(moves, myPosition, capture);
                } else {
                    moves.add(new ChessMove(myPosition, capture, null));
                }
            }
        }
    }
}

