package server;

import javax.swing.*;
import java.util.Map;

public class ServerGUIWrapper {
    private JFrame frame;
    private JTextArea textArea;

    public ServerGUIWrapper() {
        frame = new JFrame("Server GUI");
        textArea = new JTextArea(20, 50);
        frame.add(new JScrollPane(textArea));
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void updateGUI(Map<String, Integer> applianceData) {
        SwingUtilities.invokeLater(() -> {
            textArea.setText("");
            applianceData.forEach((name, power) -> textArea.append(name + ": " + power + "W\n"));
        });
    }
}



