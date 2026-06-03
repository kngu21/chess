package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        int port;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        else{
            port = 8080;
        }
        server.run(port);
        System.out.println("♕ 240 Chess Server");
    }
}
