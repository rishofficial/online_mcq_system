package com.example.demo1;
import com.example.demo1.datatypes.Info;
import com.example.demo1.threads.ClientHandler;
import com.example.demo1.threads.socketWrap;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    public static void main(String[] args) throws IOException {
        System.out.println("Server is running...");
        try(ServerSocket serverSocket=new ServerSocket(Info.port)){
            System.out.println("Server started");
            while(true){
                Socket clientSocket = serverSocket.accept();
                //System.out.println("Client connected: " + clientSocket.getInetAddress());
                socketWrap socketwrap = new socketWrap(clientSocket);
                new Thread(new ClientHandler(socketwrap)).start();
            }
        }catch (IOException e) {
            System.out.println("Error in server: " + e.getMessage());
        }
    }
}
