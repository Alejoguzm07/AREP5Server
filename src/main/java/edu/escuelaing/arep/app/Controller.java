package edu.escuelaing.arep.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Hello world!
 *
 */
public class Controller
{
    public static void main( String[] args ) throws IOException
    {
    	ExecutorService executor = Executors.newCachedThreadPool();
        try {
            AppServer.initialize();
            ServerSocket servidor = new Server().socketServidor();
            while(true) {
            	Socket cliente = new Client().socketCliente(servidor);
            	executor.execute(new AppServer(cliente));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
