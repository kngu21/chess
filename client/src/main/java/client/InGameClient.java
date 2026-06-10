package client;

import chess.*;

import java.util.*;

import static client.PostLoginClient.*;
import static java.lang.Character.getNumericValue;
import static ui.EscapeSequences.*;

public class InGameClient {
    private final ChessGame game;
    private final ChessGame.TeamColor userColor;

    public InGameClient(ChessGame.TeamColor userColor, ChessGame game){
        this.userColor = userColor;
        this.game = game;
    }

    public void run(){
        System.out.println("♕ Welcome to 240 chess. Type Help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                if(result.equals("leave")){
                    System.out.println();
                    System.exit(0);
                }
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public void printPrompt(){
        System.out.print(SET_TEXT_COLOR_WHITE + "\n"+"[PLAY_GAME] >>> ");
    }

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "move" -> {if (params.length < 2) {
                    yield "Usage: move <start> <end> [promotion]";}
                    String promo = params.length >= 3 ? params[2] : null;
                    yield move(params[0], params[1], promo);
                }
                case "resign" -> resign();
                case"highlight" -> highlight(params[0]);
                case "leave" -> "leave";
                default -> "unknown command";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String highlight(String position) {
        String result;
        if (position == null || !(position.length() == 2)) {
            return "Invalid move notation, please try again.";
        }
        if(!Character.isAlphabetic(position.charAt(0)) || !Character.isDigit(position.charAt(1))){
            return "First character must be a letter and second character must be a digit.";
        }
            int col = position.charAt(0) - 'a' + 1;
            int row = getNumericValue(position.charAt(1));
            ChessPosition start = new ChessPosition(row, col);
            while (true) {
                if (!ChessPiece.onBoard(start)) {
                    System.out.print("One or more position is off the chess baord.");
                }
                else {
                    result = "Possible moves.";
                    break;
                }
            }
            printHighlighted(start);
        return result;
    }

    private void printHighlighted(ChessPosition position){
        List<Character> whiteList = new ArrayList<>(List.of('a','b','c','d','e','f','g','h'));
        List<Character> blackList = new ArrayList<>(List.of('h','g','f','e','d','c','b','a'));
        Collection<ChessMove> valid = game.validMoves(position);
        if(Objects.equals(userColor, ChessGame.TeamColor.WHITE)) {
            borderRow(whiteList);
            for (int i = 0; i < 8; i++) {
                System.out.printf(SET_BG_COLOR_DARK_GREY + "\u2003%s ", (i - 8) * -1);
                for (int j = 0; j < 8; j++) {
                    ChessPosition square = new ChessPosition(8-i, j+1);
                    boolean isStart = square.equals(position);
                    boolean isValid = valid.stream().anyMatch(m -> m.getEndPosition().equals(square));
                    String backGround;
                    if(isStart){
                        backGround = SET_BG_COLOR_MAGENTA;
                    }
                    else if(isValid && (i+j) % 2 == 0){
                        backGround = SET_BG_COLOR_LIGHT_GREY;
                    }
                    else if(isValid && !((i+j) % 2 == 0)){
                        backGround = SET_BG_COLOR_GREEN;
                    }
                    else if((i+j) % 2 == 0){
                        backGround = SET_BG_COLOR_BLUE;
                    }
                    else{
                        backGround = SET_BG_COLOR_DARK_GREEN;
                    }
                    System.out.printf(backGround + returnPiece(game.getBoard()
                            .getPiece(new ChessPosition(8-i, j+1)))
                            + RESET_TEXT_COLOR + RESET_BG_COLOR);
                }
                System.out.print(String.format(SET_BG_COLOR_DARK_GREY + " %s\u2003",
                        (i - 8) * -1) + RESET_BG_COLOR + "\n");
            }
            borderRow(whiteList);
        }
        else if(Objects.equals(userColor, ChessGame.TeamColor.BLACK)){
            borderRow(blackList);
            for(int i = 0; i < 8; i++) {
                System.out.printf(SET_BG_COLOR_DARK_GREY + "\u2003%s ", i+1);
                for (int j = 0; j < 8; j++) {
                    ChessPosition square = new ChessPosition(i+1, 8-j);
                    boolean isStart = square.equals(position);
                    boolean isValid = valid.stream().anyMatch(m -> m.getEndPosition().equals(square));
                    String backGround;
                    if(isStart){
                        backGround = SET_BG_COLOR_MAGENTA;
                    }
                    else if(isValid && (i+j) % 2 == 0){
                        backGround = SET_BG_COLOR_LIGHT_GREY;
                    }
                    else if(isValid && (i+j) % 2 != 0){
                        backGround = SET_BG_COLOR_GREEN;
                    }
                    else if((i+j) % 2 == 0){
                        backGround = SET_BG_COLOR_BLUE;
                    }
                    else{
                        backGround = SET_BG_COLOR_DARK_GREEN;
                    }
                    System.out.printf(backGround + returnPiece(game.getBoard()
                            .getPiece(new ChessPosition(i+1, 8-j)))
                            + RESET_TEXT_COLOR + RESET_BG_COLOR);
                }
                System.out.print(String.format(SET_BG_COLOR_DARK_GREY + " %s\u2003", i+1)
                        + RESET_BG_COLOR + "\n");
            }
            borderRow(blackList);
        }
    }

    private String resign() {
        System.out.print("Are you sure you want to resign? You will forfeit and the game will be over.");
        System.out.print("Reply 'yes' or 'no'.");
        Scanner scanner = new Scanner(System.in);
        String cased = scanner.toString().toLowerCase();
        String result;
        while(true) {
            if (cased.equals("yes")) {
                 result = "You have resigned.";
                 break;
            } else if (cased.equals("no")) {
                result = "Resignation cancelled, you may continue playing.";
                break;
            } else {
                System.out.print("Invalid response");
            }
        }
        return result;
    }

    private String move(String start, String end, String promotionPiece) throws InvalidMoveException {
        if (start == null || !(start.length() == 2) || end == null || !(end.length() == 2)) {
            return "Invalid move notation, please try again.";
        }
        if(!Character.isAlphabetic(start.charAt(0)) || !Character.isDigit(start.charAt(1)) || !Character.isAlphabetic(end.charAt(0)) || !Character.isDigit(end.charAt(1))){
            return "First character must be a letter and second character must be a digit.";
        }
        ChessPosition startPos = new ChessPosition(Character.getNumericValue(start.charAt(1)),start.charAt(0) - 'a' + 1);
        ChessPosition endPos = new ChessPosition(Character.getNumericValue(end.charAt(1)),end.charAt(0) - 'a' + 1);

        if(promotionPiece != null && game.getBoard().getPiece(startPos).getPieceType() == (ChessPiece.PieceType.PAWN)) {
            if (ChessPiece.PieceType.valueOf(promotionPiece.toUpperCase()) == ChessPiece.PieceType.ROOK ||
                    ChessPiece.PieceType.valueOf(promotionPiece.toUpperCase()) == ChessPiece.PieceType.QUEEN ||
                    ChessPiece.PieceType.valueOf(promotionPiece.toUpperCase()) == ChessPiece.PieceType.KNIGHT ||
                    ChessPiece.PieceType.valueOf(promotionPiece.toUpperCase()) == ChessPiece.PieceType.BISHOP) {
                ChessMove move = new ChessMove(startPos, endPos, ChessPiece.PieceType.valueOf(promotionPiece.toUpperCase()));
                game.makeMove(move);
                drawGame(game, String.valueOf(userColor));
                return String.format("Promoted from pawn %s to %s %s", start, promotionPiece, end);
            }
        }
        else if(promotionPiece == null){
            game.makeMove(new ChessMove(startPos, endPos, null));
            drawGame(game,String.valueOf(userColor));
            return String.format("Made move %s to %s", start, end);
        }
        else{
            return "Invalid promotion piece.";
        }
        return "Only pawns can be promoted.";
    }

    private String redraw() {
        drawGame(game, game.getTeamTurn().toString());
        return "Redrew current game board.";
    }


    public String help(){
        return SET_TEXT_COLOR_BLUE+"move <START> <END> <PROMOTION_PIECE>" + SET_TEXT_COLOR_MAGENTA + "- start and end positions\n" +
                SET_TEXT_COLOR_BLUE+"redraw " + SET_TEXT_COLOR_MAGENTA + "- chessboard\n" +
                SET_TEXT_COLOR_BLUE+"leave " + SET_TEXT_COLOR_MAGENTA + "- current game\n" +
                SET_TEXT_COLOR_BLUE+"resign" + SET_TEXT_COLOR_MAGENTA + "- forfeits game\n" +
                SET_TEXT_COLOR_BLUE+"highlight <POSITION> " + SET_TEXT_COLOR_MAGENTA + "- shows legal moves\n" +
                SET_TEXT_COLOR_BLUE+"help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
    }
}