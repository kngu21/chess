package client;

import chess.ChessGame;
import dataaccess.DataAccessException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class InGameClient {
    private ChessGame game;
    private final ServerFacade facade;
    public InGameClient(String serverUrl){
        facade = new ServerFacade(serverUrl);
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
        System.out.print(SET_TEXT_COLOR_WHITE + "\n"+"[LOGGED_OUT] >>> ");
    }

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move();
                case "resign" -> resign();
                case"highlight" -> highlight();
                default -> "unknown command";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String highlight() {

    }

    private String resign() {
        System.out.print("Are you sure you want to resign? You will forfeit and the game will be over.");
        System.out.print("Reply 'yes' or 'no'.");
        Scanner scanner = new Scanner(System.in);
        String cased = scanner.toString().toLowerCase();
        String result = "";
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

    private String move(){

    }

    private String redraw() {
        PostLoginClient.drawGame(game, game.getTeamTurn().toString());
        return "Redrew current game board.";
    }


    public String help(){
        return SET_TEXT_COLOR_BLUE+"move <START> <END>" + SET_TEXT_COLOR_MAGENTA + "- start and end positions\n" +
                SET_TEXT_COLOR_BLUE+"redraw" + SET_TEXT_COLOR_MAGENTA + "- chessboard\n" +
                SET_TEXT_COLOR_BLUE+"leave" + SET_TEXT_COLOR_MAGENTA + "- current game\n" +
                SET_TEXT_COLOR_BLUE+"resign" + SET_TEXT_COLOR_MAGENTA + "forfeits game\n" +
                SET_TEXT_COLOR_BLUE+"highlight <POSITION>" + SET_TEXT_COLOR_MAGENTA + "shows legal moves\n" +
                SET_TEXT_COLOR_BLUE+"help" + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
    }
}