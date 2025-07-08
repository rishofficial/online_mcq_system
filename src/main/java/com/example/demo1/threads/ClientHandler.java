package com.example.demo1.threads;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run(){
        try(BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String command = input.readLine();
            if(command.equals("CREATE")){
                String name = input.readLine();
                name=name.replace(" ", "_");
                String age = input.readLine();
                String userId = input.readLine();
                String password = input.readLine();
                String confirmPassword = input.readLine();
                String role = input.readLine();
                System.out.println("Received from client: " + userId + ", " + password + ", " + role);

                String resourceFile = role.equals("Teacher") ? "assets/accounts/Teacher.txt" : "assets/accounts/Student.txt";
                System.out.println(resourceFile);
                try (Scanner scanner = new Scanner(new File(resourceFile))) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(" ");
                        if (parts[0].equals(userId) ) {
                            System.out.println("Duplicate for " + role + ": " + userId);
                            output.println("NO");
                            return;
                        }
                    }
                }
                File file = new File(resourceFile);
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write( "\n" + userId + " " + name + " " + age + " " + password);
                } catch (IOException e) {
                    output.println("Error writing to file. Please try again.");
                }
                output.println("YES");
                System.out.println("Account created successfully for " + role + ": " + userId);
            }
            else if(command.equals("LOGIN")){
                String userId = input.readLine();
                String password = input.readLine();
                String role = input.readLine();
                System.out.println("Received from client: " + userId + ", " + password + ", " + role);

                String resourceFile = role.equals("Teacher") ? "assets/accounts/Teacher.txt" : "assets/accounts/Student.txt";
                System.out.println("Resource file: " + new File(resourceFile).exists());
                try (Scanner scanner = new Scanner(new File(resourceFile))) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(" ");
                        if (parts[0].equals(userId) && parts[3].equals(password)) {
                            System.out.println("Login successful for " + role + ": " + userId);
                            output.println("YES");
                            return;
                        }
                    }
                }
                System.out.println("Login failed for " + role + ": " + userId);
                output.println("NO");
            }
            else{
                System.out.println("Unknown command: " + command);
                output.println("UNKNOWN_COMMAND");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
