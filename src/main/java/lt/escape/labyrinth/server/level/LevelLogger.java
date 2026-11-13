package lt.escape.labyrinth.server.level;

import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility that displays level construction details
 * in a separate GUI window.
 */
public class LevelLogger {

    private static JFrame logWindow;
    private static JTextArea logArea;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void openWindow(String levelName) {
        if (logWindow == null) {
            logWindow = new JFrame("Level Logger - " + levelName);
            logWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            logWindow.setSize(600, 400);
            logWindow.setLocationRelativeTo(null);

            logArea = new JTextArea();
            logArea.setEditable(false);
            logArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(logArea);
            logWindow.add(scrollPane);
            logWindow.setVisible(true);
        }
    }

    public static void log(String message) {
        if (logArea != null) {
            String timestamp = LocalTime.now().format(TIME_FORMAT);
            String formattedMessage = "[" + timestamp + "] " + message + "\n";
            logArea.append(formattedMessage);
            logArea.setCaretPosition(logArea.getDocument().getLength());
            System.out.println(formattedMessage);
        }
    }

    public static void clearLog() {
        if (logArea != null) {
            logArea.setText("");
        }
    }
}
