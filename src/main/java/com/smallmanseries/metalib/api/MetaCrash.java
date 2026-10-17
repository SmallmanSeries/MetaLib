package com.smallmanseries.metalib.api;

import com.mojang.blaze3d.platform.Window;
import com.smallmanseries.metalib.client.fakecrash.FakeCrashPanel;
import com.smallmanseries.metalib.util.HeadlessFix;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Classic Meta game operation: force crash.
 * <p>
 * This class provides some operations for causing crashes, including triggering a fake crash panel or making the game actually crash.
 * All methods should be called on the client side.
 *
 * @author KiTheAdventurer
 * @see FakeCrashPanel
 * @since 1.0
 */
public class MetaCrash {

    /**
     * Display a fake crash report panel to the player, which inspired by the crash panel from Minecraft 1.6.
     * <p>A dark blue background, a game logo is positioned in the center at the top, with the report text below.
     *
     * @param title  the title of the Crash Report window
     * @param report report text
     * @param after  what action will be taken after the player closes the report window
     */
    public static void showFakeCrashReport(String title, String report, Runnable after) {
        HeadlessFix.ensureNonHeadless();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = getFrame(title, after);
            frame.add(new FakeCrashPanel(report));
            frame.setVisible(true);
        });
    }

    // Todo Crash report that show an image instead of some message

    /**
     * Save the game, and then crash it.
     * <p>This is just an example, where a simple crash report is generated using {@link RuntimeException}. In practice, it is recommended to write your own crash logic, as there are more customizable parts.
     *
     * @param description crash description, referring to the content of the "Description" section in the crash report
     * @param report      the main body of the report, typically containing the cause of the crash
     */
    public static void forceCrash(String description, String report) {
        // MetaStorage.flush();

        CrashReport crashreport = CrashReport.forThrowable(new RuntimeException(report), description);
        Minecraft.getInstance().emergencySaveAndCrash(crashreport);
    }

    /**
     * Make the game save and exit silently without generating a crash report.
     */
    public static void silentQuit() {
        // MetaStorage.flush();

        Minecraft.getInstance().stop();
    }

    /**
     * Wait for the specified time, then save and exit the game silently without generating a crash report.
     *
     * @param delayMs delay time (in milliseconds)
     */
    public static void silentQuitAfter(int delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ignored) {
            }
            silentQuit();
        }, "MetaLib-QuitDelay").start();
    }

    private static JFrame getFrame(String title, Runnable after) {
        Window mcwindow = Minecraft.getInstance().getWindow();
        JFrame frame = new JFrame(title);

        if (mcwindow.isFullscreen()) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else if (mcwindow.isMinimized()) {
            frame.setSize(854, 480);
        } else {
            GraphicsConfiguration gc = frame.getGraphicsConfiguration();
            frame.setSize((int) (mcwindow.getWidth() / gc.getDefaultTransform().getScaleX()) + 15, (int) (mcwindow.getHeight() / gc.getDefaultTransform().getScaleY()) + 38);
            frame.setLocation((int) (mcwindow.getX() / gc.getDefaultTransform().getScaleX()) - 7, (int) (mcwindow.getY() / gc.getDefaultTransform().getScaleY()) - 30);
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (after != null) {
                    after.run();
                }
            }
        });
        frame.setLayout(new BorderLayout());
        return frame;
    }
}
