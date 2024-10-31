package io.codeforall.fanstatics;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {
    private final Socket clientSocket;
    private final ArrayList<Client> clients;
    private BufferedReader in;
    private PrintWriter out;
    private String name;

    public Client(Socket clientSocket, ArrayList<Client> clients) throws IOException {
        this.clients = clients;
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        String line;
        try {
            // Client enters the chat and enters name
            out.println("Please enter your name.");
            out.flush();
            this.name = in.readLine();
            System.out.println(name + " has entered the chat.");
            this.broadcast(" has entered the chat.");

            mainWhile:
            while (true) {
                // Client input
                line = in.readLine();
                String command = line.split(" ")[0];
                line = line.replaceFirst(command + " ", "");

                switch (command) {
                    case "/help":
                        this.help();
                        break;
                    case "/list":
                        this.list();
                        break;
                    case "/changename":
                        String newName = line.split(" ")[0];
                        this.changeName(newName);
                        break;
                    case "/broadcast":
                        this.broadcast(" " + line);
                        break;
                    case "/whisper":
                        String destName = line.split(" ")[0];
                        line = line.replaceFirst(destName, "");
                        this.whisper(destName, line);
                        break;
                    case "/quit":
                        break mainWhile;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Close the socket
        try {
            out.println("Connection closed.");
            System.out.println("Connection closed.");
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message) {
        for (Client client : this.clients) {
            if (client != this) {
                client.out.println(this.name + ":" + message);
            }
        }
    }

    public void whisper(String destName, String message) {
        for (Client client : this.clients) {
            if (client.name.equals(destName)) {
                client.out.println("(" + this.name + "):" + message);
            }
        }
    }

    public void changeName(String newName) {
        this.broadcast(" Changed name to " + newName);
        this.name = newName;
    }

    public void list() {
        this.out.println("Users in the chat:");
        for (Client client : this.clients) {
            if (client != this) {
                this.out.println(client.name);
            }
        }
    }

    public void help() {
        this.out.println("Available commands:");
        this.out.println("/broadcast <message> - Sends the message to everyone.");
        this.out.println("/whisper <destination> <message> - Sends the message only to destination user.");
        this.out.println("/list or /help - List available chat commands.");
        this.out.println("/quit - Quits the chat.");
    }
}
