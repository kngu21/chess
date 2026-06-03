package client;

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
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                if(result.equals("quit")){
                    System.out.println();
                    System.exit(0);
                }
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            if(state == State.LOGGEDIN){
                new PostLoginClient(facade).run();
            }
            state = State.LOGGEDOUT;
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
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                case "help" -> help();
                default -> "unknown command";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String login(String [] params) throws Exception {
        if (params.length < 2) {
            return "Usage: login <username> <password>\n";
        }
        try{boolean success = facade.login(params[0], params[1]);
            if (success){
                state = State.LOGGEDIN;
                return String.format("Logged in as %s.", params[0]);
            }
            else{
                return "";
            }
        } catch(Exception e){
            throw new Exception("incorrect input");
        }
    }

    public String register(String [] params) throws Exception {
        if (params.length < 3) {
            return "Usage: register <username> <password> <email>\n";
        }
        try{boolean success = facade.register(params[0], params[1], params[2]);
            if (success){
                state = State.LOGGEDIN;
                return String.format("Registered and logged in as %s.", params[0]);
            }
            else{
                return "";
            }
        } catch(Exception e){
            throw new Exception("incorrect input");
        }
    }

    public String help(){
        return SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_MAGENTA + "- to create an account\n" +
                SET_TEXT_COLOR_BLUE+"login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_MAGENTA + "- to play chess\n" +
                SET_TEXT_COLOR_BLUE+"quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE+"help" + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
    }
}
