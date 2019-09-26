package edu.escuelaing.arep.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
	
	public static Socket socketCliente(ServerSocket serverSocket) {
        try {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Recieving...");
            return clientSocket;
        } catch (IOException e) {
        	System.out.println("Accept failed.");
            System.exit(1);
            return null;
        }
    }

}
