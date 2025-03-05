
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import se.mau.DA343A.VT25.projekt.net.SecurityTokens;

public class Client {
    private String applianceName;
    private int maxPower;
    private int powerUsage;
    private ObjectOutputStream out;
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5050;
    private SecurityTokens SecurityTokenGenerator;

    public Client(String applianceName, int maxPower) {
        this.applianceName = applianceName;
        this.maxPower = maxPower;
        setupGUI();
        connectToServer();
    }

    private void setupGUI() {
        JFrame frame = new JFrame("Smart Plug - " + applianceName);
        JSlider slider = new JSlider(0, maxPower, 0);
        JLabel label = new JLabel(applianceName + " Förbrukning: 0W");

        slider.addChangeListener(e -> {
            powerUsage = slider.getValue();
            label.setText(applianceName + " Förbrukning: " + powerUsage + "W");
            sendPowerUsage();
        });

        frame.setLayout(new BorderLayout());
        frame.add(label, BorderLayout.NORTH);
        frame.add(slider, BorderLayout.CENTER);
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    out.close();
                } catch (IOException ignored) {}
            }
        });
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());

            // Skicka team-namn till konstruktorn
            String teamName = "DittTeamNamn"; // Byt ut detta mot ditt faktiska teamnamn!
            se.mau.DA343A.VT25.projekt.net.SecurityTokens tokenGenerator = new se.mau.DA343A.VT25.projekt.net.SecurityTokens(teamName);
            String token = tokenGenerator.generateToken();

            if (token == null) {
                System.out.println("❌ ERROR: Kunde inte generera token!");
                return;
            }

            out.writeObject(token);
            out.writeObject(applianceName);
            out.writeObject(0);
            out.flush();

            System.out.println("✅ Ansluten till servern med token: " + token);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void sendPowerUsage() {
        new Thread(() -> {
            try {
                if (out != null) {
                    out.writeObject(powerUsage);
                    out.flush();
                    System.out.println("📡 Skickade: " + powerUsage + "W");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        new Client("Laptop", 30);
        new Client("TV", 150);
    }
}

