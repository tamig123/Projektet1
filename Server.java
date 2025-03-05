package server;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final int PORT = 5050;
    private Map<String, Integer> applianceData = new ConcurrentHashMap<>();
    private server.ServerGUIWrapper gui;

    public Server() {
        gui = new server.ServerGUIWrapper();
        startServer();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Servern körs på port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 Ny klient ansluten: " + clientSocket.getInetAddress());

                // Starta en tråd för klienten
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateConsumption(String appliance, int consumption) {
        System.out.println("⚡ Uppdaterar: " + appliance + " → " + consumption + "W");
        applianceData.put(appliance, consumption);

        // Uppdatera GUI på rätt tråd
        SwingUtilities.invokeLater(() -> gui.updateGUI(applianceData));
    }


    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            String token = (String) in.readObject();
            String applianceName = (String) in.readObject();
            int initialConsumption = (int) in.readObject();

            System.out.println("📡 Klient " + applianceName + " ansluten med token: " + token);
            applianceData.put(applianceName, initialConsumption);
            gui.updateGUI(applianceData);

            // Läs kontinuerligt nya förbrukningsvärden
            while (true) {
                int newConsumption = (int) in.readObject();
                System.out.println("⚡ " + applianceName + " Förbrukning: " + newConsumption + "W");
                applianceData.put(applianceName, newConsumption);
                gui.updateGUI(applianceData);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Klient kopplades bort.");
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}



