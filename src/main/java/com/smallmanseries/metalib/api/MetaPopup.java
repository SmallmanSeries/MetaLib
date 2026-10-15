package com.smallmanseries.metalib.api;

import com.smallmanseries.metalib.util.HeadlessFix;

import javax.swing.*;
import java.awt.*;

/**
 * Classic Meta game operation: show pop-ups
 * <p>
 * This class provides operations to show pop-ups on the player's computer.
 * All methods should be called on the client side.
 *
 * @author KiTheAdventurer
 * @since 1.0
 */
public class MetaPopup {

    /**
     * The type of pop-up window, which determines the icon on the left side of the window.
     */
    public enum Type {INFO, WARNING, ERROR, QUESTION, PLAIN}

    /**
     * Show a {@link JOptionPane} pop-up window.
     * Call this on the client side to ensure that the pop-up window appears on the player's computer.
     *
     * @param title   Pop-up title
     * @param message Pop-up content
     * @param type    Pop-up type ({@link Type})
     */
    public static void showPopup(String title, String message, Type type) {
        HeadlessFix.ensureNonHeadless();
        SwingUtilities.invokeLater(() -> {
            int msgType = switch (type) {
                case INFO -> JOptionPane.INFORMATION_MESSAGE;
                case WARNING -> JOptionPane.WARNING_MESSAGE;
                case ERROR -> JOptionPane.ERROR_MESSAGE;
                case QUESTION -> JOptionPane.QUESTION_MESSAGE;
                case PLAIN -> JOptionPane.PLAIN_MESSAGE;
            };
            JOptionPane.showMessageDialog(null, message, title, msgType);
        });
    }

    /**
     * Show a {@link JOptionPane} pop-up window with a fake stack trace, simulate an error pop-up.
     * Call this on the client side to ensure that the pop-up window appears on the player's computer.
     *
     * @param title     Pop-up title
     * @param message   Pop-up content
     * @param type      Pop-up type ({@link Type})
     * @param fakeStack Fake stack trace information (in fact, any text can be written here, such as what the meta character says)
     */
    public static void showPopup(String title, String message, Type type, String fakeStack) {
        HeadlessFix.ensureNonHeadless();
        SwingUtilities.invokeLater(() -> {
            JTextArea area = new JTextArea(fakeStack, 12, 60);
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.add(new JLabel(message), BorderLayout.NORTH);
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            int msgType = switch (type) {
                case INFO -> JOptionPane.INFORMATION_MESSAGE;
                case WARNING -> JOptionPane.WARNING_MESSAGE;
                case ERROR -> JOptionPane.ERROR_MESSAGE;
                case QUESTION -> JOptionPane.QUESTION_MESSAGE;
                case PLAIN -> JOptionPane.PLAIN_MESSAGE;
            };
            JOptionPane.showMessageDialog(null, panel, title, msgType);
        });
    }
}
