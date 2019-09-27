package edu.escuelaing.arep.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.stream.events.StartDocument;

/**
 * Hello world!
 *
 */
public class Controller
{
    public static void main( String[] args ) throws IOException
    {
    	try {
			AppServer.initialize();
			start();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void start() {
    	ExecutorService executor = Executors.newCachedThreadPool();
    	ServerSocket servidor = new Server().socketServidor();       
        while(true) {
         	Socket cliente = new Client().socketCliente(servidor);
           	executor.execute(new AppServer(cliente));
        }
    }
}
