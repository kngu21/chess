package client;

import service.BadRequestException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class PostLoginClient {
    public void run(){
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
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
                case "create" -> createGame(Integer.parseInt(params[0]));
                case "list" -> listGames();
                case "join" -> joinGame(Integer.parseInt(params[0]), params[1]);
                case "observe" -> observeGame(Integer.parseInt(params[0]));
                case "quit" -> "quit";
                case "help" -> help();
                default -> "unknown command";
            };
        } catch (BadRequestException ex) {
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
        return "";
    }

    public String createGame(int gameID){
        return "";
    }

    public String listGames(){
        return "";
    }

    public String joinGame(int gameID, String color){
        return "";
    }

    public String observeGame(int gameID){
        return "";
    }
}
