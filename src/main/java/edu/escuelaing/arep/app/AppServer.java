package edu.escuelaing.arep.app;

import edu.escuelaing.arep.app.anotations.Web;
import edu.escuelaing.arep.app.interfaces.Handler;

import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppServer extends Thread{
    private static ConcurrentHashMap<String, Handler>  listURLHandler;
    private static Socket client;


    public AppServer(Socket client) {        
        this.client = client;
    } 
    
    public static void initialize() throws ClassNotFoundException {
    	listURLHandler = new ConcurrentHashMap<String, Handler>();
        File f = new File(System.getProperty("user.dir") + "/src/main/java/apps");
        File[] ficheros = f.listFiles();
        for (int x=0;x<ficheros.length;x++){
            String name = ficheros[x].getName();
            load("apps." + name.substring(0,name.indexOf(".")));
            System.out.println("Class " + name.substring(0,name.indexOf(".")) + " was loaded.");
        }
    }

    public void run() {
            try {
                PrintWriter out = null;
                out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Request: "+ inputLine +" recieved");
                    if(inputLine.contains("GET")) {
                        String address = inputLine.split(" ")[1];
                        if(address.contains("/apps/")) {
                            out.println("HTTP/1.1 200 OK\r");
                            out.println("Content-Type: text/html\r");
                            out.println("\r\n");
                            System.out.println(address);
                            try {
                            	String[] parameters = address.split("\\?");
                                if(parameters.length == 1){
                                	out.println(listURLHandler.get(address).process(null));
                                }else {                            	
                                	address = address.substring(0,address.indexOf("?"));
                                	String[] arguments = parameters[1].split(",");                            	
                                	out.println(listURLHandler.get(address).process(arguments));
                                }
                            }catch (Exception e) {
                            	String volver = "<!DOCTYPE html>"
                                		+ "<html>"
                                		+ "<body>"
                                		+ "<h1>OOOOPS!!!</h1>"
                                		+ " <a href='"+ System.getProperty("user.dir") + "/static/index.html'>volver al inicio</a>"
                                		+ "</body>"
                                		+ "</html>";                                    
                                out.println(volver);
							}
                                                        
                        }if(address.contains("/static/")){                        	
                            System.out.println(address);
                            String[] parts = address.split("/");
                            String resource = parts[parts.length - 1];
                            if(resource.contains(".html")) {
                            	out.println("HTTP/1.1 200 OK\r");
                                out.println("Content-Type: text/html\r");
                                out.println("\r\n");
                                try {
                                    BufferedReader resourceReader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(System.getProperty("user.dir") + "/static/" + resource), "UTF8"));
                                    while (resourceReader.ready()) {
                                        out.println(resourceReader.readLine());
                        			}
                                    resourceReader.close();
                                }catch (Exception e) {
                                	String volver = "<!DOCTYPE html>"
                                    		+ "<html>"
                                    		+ "<body>"
                                    		+ "<h1>OOOOPS!!!</h1>"
                                    		+ " <a href='"+ System.getProperty("user.dir") + "/static/index.html'>volver al inicio</a>"
                                    		+ "</body>"
                                    		+ "</html>";                                    
                                    out.println(volver);
                                }
                            }else if(resource.contains(".gif") || resource.contains(".jpeg") || resource.contains(".jpg") || resource.contains(".png")){
                            	String format = resource.substring(resource.indexOf(".") + 1);
                            	try {
	                            	BufferedImage img = ImageIO.read(new File(System.getProperty("user.dir") + "/static/" + resource));
	                            	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	                            	ImageIO.write(img, format, bytes);
	                            	byte [] bytesList = bytes.toByteArray();
	                            	DataOutputStream imgOut = new DataOutputStream(client.getOutputStream());
	                            	imgOut.writeBytes("HTTP/1.1 200 OK \r\n");
	                            	imgOut.writeBytes("Content-Type: image/" + format + "\r\n");
	                            	imgOut.writeBytes("Content-Length: " + bytesList.length);
	                            	imgOut.writeBytes("\r\n\r\n");
	                    			imgOut.write(bytesList);
	                    			imgOut.close();
	                    			out.println(imgOut.toString());
                            	}catch (Exception e) {
                            		out.println("HTTP/1.1 200 OK\r");
                                    out.println("Content-Type: text/html\r");
                                    out.println("\r\n");
                                    String volver = "<!DOCTYPE html>"
                                    		+ "<html>"
                                    		+ "<body>"
                                    		+ "<h1>OOOOPS!!!</h1>"
                                    		+ " <a href='"+ System.getProperty("user.dir") + "/static/index.html'>volver al inicio</a>"
                                    		+ "</body>"
                                    		+ "</html>";                                    
                                    out.println(volver);
								}
                    			
                            }else {
                            	out.println("HTTP/1.1 200 OK\r");
                                out.println("Content-Type: text/html\r");
                                out.println("\r\n");
                                String volver = "<!DOCTYPE html>"
                                		+ "<html>"
                                		+ "<body>"
                                		+ "<h1>OOOOPS!!!</h1>"
                                		+ " <a href='"+ System.getProperty("user.dir") + "/static/index.html'>volver al inicio</a>"
                                		+ "</body>"
                                		+ "</html>";                                    
                                out.println(volver);
                            }
                        }
                    }
                    if (!in.ready()) {
                        break;
                    }
                }
                in.close();
                out.close();
                //client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        
    }

    public static void load(String classpath){
        Class c = null;
        try {
            c = Class.forName(classpath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for(Method m: c.getDeclaredMethods()){
            if(m.getAnnotation(Web.class) != null){
                listURLHandler.put("/apps" + m.getAnnotation(Web.class).value(), new StaticMethodHandler(m));
            }
        }
    }
}
