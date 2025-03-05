package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectionHandler extends Thread {
    private Socket clientSocket;
    private server.Server server;
    private String applianceName;

    public ClientConnectionHandler(Socket clientSocket, server.Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            String token = (String) in.readObject();
            if (!validateToken(token)) {
                clientSocket.close();
                return;
            }

            applianceName = (String) in.readObject();
            int initialConsumption = (int) in.readObject();
            server.updateConsumption(applianceName, initialConsumption);

            while (true) {
                int newConsumption = (int) in.readObject();
                server.updateConsumption(applianceName, newConsumption);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Klient kopplades bort: " + applianceName);
        }
    }

    private boolean validateToken(String token) {
        return token != null && token.length() > 5;
    }
}


