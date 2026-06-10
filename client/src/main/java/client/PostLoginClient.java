package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameInfo;

import java.io.IOException;
import java.util.*;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class PostLoginClient {
    private final ServerFacade facade;
    public PostLoginClient(ServerFacade facade){
        this.facade = facade;
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                if (result.equals("Logged out.")) {
                    System.out.print(result);
                    break;
                }
                else if(result.equals("quit")){
                    System.exit(0);
                }
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt(){
        System.out.print(SET_TEXT_COLOR_WHITE + "\n"+"[LOGGED_IN] >>> ");
    }

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> {if(params.length == 0){yield "Usage: create <gameName>";} else{yield createGame(params[0]);}}
                case "list" -> listGames();

                case "join" -> {if(params.length < 2){
                    yield "Usage: join <gameID> <color>";}
                    List<GameInfo> games = facade.getGames().games();
                    try{ Integer.parseInt(params[0]);
                    } catch(NumberFormatException e){yield "invalid number, must be an integer";}
                    if(Integer.parseInt(params[0]) <= 0 || Integer.parseInt(params[0]) > games.toArray().length){
                        yield "invalid gameID";
                    }
                    yield joinGame(games.get(Integer.parseInt(params[0]) - 1).gameID(), params[1]);
                }

                case "observe" -> {if(params.length == 0){
                    yield "Usage: observe <gameID>";}
                    List<GameInfo> games = facade.getGames().games();
                    try{ Integer.parseInt(params[0]);
                    } catch(NumberFormatException e){yield "invalid number, must be an integer";}
                    if(Integer.parseInt(params[0]) <= 0 || Integer.parseInt(params[0]) > games.toArray().length){
                        yield "invalid gameID";
                    }
                    yield observeGame(facade.getGames().games().get(Integer.parseInt(params[0]) - 1).gameID());
                }

                case "quit" -> "quit";
                case "help" -> help();
                default -> "unknown command";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        return SET_TEXT_COLOR_BLUE+"create <NAME> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" +
                SET_TEXT_COLOR_BLUE+"list " + SET_TEXT_COLOR_MAGENTA + "- games\n" +
                SET_TEXT_COLOR_BLUE+"join <ID> [WHITE|BLACK]" + SET_TEXT_COLOR_MAGENTA + "- a game\n" +
                SET_TEXT_COLOR_BLUE+"observe <ID> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" +
                SET_TEXT_COLOR_BLUE+"logout " + SET_TEXT_COLOR_MAGENTA + "- when you are done\n" +
                SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE+"help" + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
    }

    public String logout(){
        try{
            boolean success = facade.logout();
            if(success){
                return "Logged out.";
            }
            else{
                return "";
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String createGame(String gameName){
        try{
            boolean success = facade.createGame(gameName);
            if(success){
                return "Created game.";
            }
            else{
                return "";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String listGames(){
        try{
            return facade.listGames();

        }catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String joinGame(int gameID, String color) throws IOException, InterruptedException {
        if(!color.equalsIgnoreCase("white") && !color.equalsIgnoreCase("black")){
            return "invalid color";
        }
        if (facade.joinGame(gameID, color.toUpperCase())) {
            System.out.print("Joined game " + gameID + " as " + color);
            System.out.println();
            ChessGame game = new ChessGame();
            drawGame(game, color.toUpperCase());
            new InGameClient(facade, ChessGame.TeamColor.valueOf(color.toUpperCase()), game).run();
        }
        else{
            return "";
        }
        return "";
    }

    public String observeGame(int gameID) throws IOException, InterruptedException {
        if(facade.observeGame(gameID)){
            System.out.print("Observing game " + gameID+ "\n");
            ChessGame game = new ChessGame();
            drawGame(game, "WHITE");
        }
        else {
            return "game not found";
        }
        return "";
    }

    public static void borderRow(List<Character> list){
        System.out.print(SET_BG_COLOR_DARK_GREY + " \u2003 " + RESET_BG_COLOR);
        for(int i = 0; i < 8; i++){
            System.out.print(SET_BG_COLOR_DARK_GREY + String.format("\u2003%s ",
                    list.get(i)) + RESET_BG_COLOR);
        }
        System.out.print(SET_BG_COLOR_DARK_GREY + " \u2003 " + RESET_BG_COLOR);
        System.out.println();
    }

    public static String returnPiece(ChessPiece piece){
        if(piece == null){
            return EMPTY;
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return switch (piece.getPieceType()){
                case KING -> SET_TEXT_COLOR_WHITE +BLACK_KING;
                case QUEEN -> SET_TEXT_COLOR_WHITE+BLACK_QUEEN;
                case BISHOP -> SET_TEXT_COLOR_WHITE+ BLACK_BISHOP;
                case KNIGHT -> SET_TEXT_COLOR_WHITE+ BLACK_KNIGHT;
                case ROOK -> SET_TEXT_COLOR_WHITE+ BLACK_ROOK;
                case PAWN -> SET_TEXT_COLOR_WHITE+BLACK_PAWN;
            };
        }
        else {
            return switch (piece.getPieceType()) {
                case KING -> SET_TEXT_COLOR_BLACK + BLACK_KING;
                case QUEEN -> SET_TEXT_COLOR_BLACK + BLACK_QUEEN;
                case BISHOP -> SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
                case KNIGHT -> SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
                case ROOK -> SET_TEXT_COLOR_BLACK + BLACK_ROOK;
                case PAWN -> SET_TEXT_COLOR_BLACK + BLACK_PAWN;
            };
        }
    }


    public static void drawGame(ChessGame game, String color){
        List<Character> whiteList = new ArrayList<>(List.of('a','b','c','d','e','f','g','h'));
        List<Character> blackList = new ArrayList<>(List.of('h','g','f','e','d','c','b','a'));
        if(Objects.equals(color, "WHITE")) {
            borderRow(whiteList);
            for (int i = 0; i < 8; i++) {
                System.out.printf(SET_BG_COLOR_LIGHT_GREY + "\u2003%s ", (i - 8) * -1);
                for (int j = 0; j < 8; j++) {
                    if ((i + j) % 2 == 0) {
                        System.out.print(SET_BG_COLOR_BLUE + returnPiece(game.getBoard()
                                .getPiece(new ChessPosition(8-i, j+1)))
                                + RESET_TEXT_COLOR + RESET_BG_COLOR);
                    } else {
                        System.out.print(SET_BG_COLOR_DARK_GREEN + returnPiece(game.getBoard()
                                .getPiece(new ChessPosition(8-i, j+1)))
                                + RESET_TEXT_COLOR + RESET_BG_COLOR);
                    }
                }
                System.out.print(String.format(SET_BG_COLOR_LIGHT_GREY + " %s\u2003",
                        (i - 8) * -1) + RESET_BG_COLOR + "\n");
            }
            borderRow(whiteList);
        }
        else if(Objects.equals(color, "BLACK")){
            borderRow(blackList);
            for(int i = 0; i < 8; i++) {
                System.out.printf(SET_BG_COLOR_LIGHT_GREY + "\u2003%s ", i+1);
                for (int j = 0; j < 8; j++) {
                    if((i+j) % 2 == 0){
                        System.out.print(SET_BG_COLOR_BLUE + returnPiece(game.getBoard()
                                .getPiece(new ChessPosition(i+1,8-j)))
                                + RESET_TEXT_COLOR+RESET_BG_COLOR);
                    }
                    else{
                        System.out.print(SET_BG_COLOR_DARK_GREEN + returnPiece(game.getBoard()
                                .getPiece(new ChessPosition(i+1,8-j)))
                                + RESET_TEXT_COLOR+RESET_BG_COLOR);
                    }
                }
                System.out.print(String.format(SET_BG_COLOR_LIGHT_GREY + " %s\u2003", i+1)
                        + RESET_BG_COLOR + "\n");
            }
            borderRow(blackList);
        }
    }
}
