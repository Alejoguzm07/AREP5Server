package edu.escuelaing.arep.app;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	
	public static ServerSocket socketServidor() {
		int port;
        if (System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        } else {
            port = 4567;
        }
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Port " + port + " is being listened...");
            return serverSocket;
        } catch (IOException e) {
        	System.out.println("Port " + port + " could not be listened...");
            System.exit(1);
            return null;
        }
    }
}
