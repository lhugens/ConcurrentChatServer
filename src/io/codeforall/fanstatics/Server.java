package io.codeforall.fanstatics;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {
        int PORT = 8080;

        ArrayList<Client> clients = new ArrayList<>();

        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executorService = Executors.newCachedThreadPool();

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                Client client = new Client(clientSocket, clients);
                clients.add(client);
                System.out.println("Connection established.");
                executorService.execute(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
