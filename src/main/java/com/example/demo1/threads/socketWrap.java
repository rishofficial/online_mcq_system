package com.example.demo1.threads;

import java.io.*;
import java.net.Socket;

public class socketWrap implements AutoCloseable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public socketWrap(String s, int port) throws IOException {
        this.socket = new Socket(s, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public socketWrap(Socket s) throws IOException {
        this.socket = s;
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String readLine() throws IOException, ClassNotFoundException {
        return input.readLine();
    }

    public void writeLine(String o) throws IOException {
        output.println(o);
    }

    @Override
    public void close() throws Exception {
        input.close();
        output.close();
    }
}
