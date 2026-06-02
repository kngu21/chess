package client;

import service.BadRequestException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PreLoginClient {
    private final ServerFacade server;
    private State state = State.LOGGEDOUT;
    public PreLoginClient(String serverUrl){
        server = new ServerFacade(serverUrl);
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
        System.out.print("\n"+"[LOGGED_OUT] >>> ");
    }

    public String eval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> logIn(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (BadRequestException ex) {
            return ex.getMessage();
        }
    }
    public String logIn(String [] params){
        return null;
    }
    public String register(String [] params){
        return null;
    }
    public String help(){
        return null;
    }
}
