package client;

import service.BadRequestException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginClient {
    private final ServerFacade facade;
    private State state = State.LOGGEDOUT;
    public PreLoginClient(String serverUrl){
        facade = new ServerFacade(serverUrl);
    }
    public void run(){
        System.out.println("♕ Welcome to 240 chess. Type Help to get started. ♕");

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
        System.out.print(SET_TEXT_COLOR_WHITE + "\n"+"[LOGGED_OUT] >>> ");
    }

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                case "help" -> help();
                default -> "unknown command";
            };
        } catch (BadRequestException ex) {
            return ex.getMessage();
        }
    }

    public String login(String [] params){
        if (params.length < 2) return "Usage: login <username> <password>\n";
        try{boolean success = facade.login(params[0], params[1]);
            if (success){
                return String.format("Logged in as %s", params[0]);
            }
            else{
                return "invalid input";
            }
        } catch(BadRequestException | IOException | InterruptedException e){
            throw new BadRequestException("incorrect input");
        }
    }

    public String register(String [] params){
        return null;
    }

    public String help(){
        return SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_MAGENTA + "- to create an account\n" +
                SET_TEXT_COLOR_BLUE+"login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_MAGENTA + "- to play chess\n" +
                SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE+"help" + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
    }
}
