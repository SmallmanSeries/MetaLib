package com.smallmanseries.metalib.client.fakecrash;

import net.minecraft.CrashReport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Crash report screen before Minecraft 1.6
 *
 * @author KiTheAdventurer
 * @see com.smallmanseries.metalib.api.MetaCrash
 * @since 1.0
 */
public class FakeCrashPanel extends Panel {

    /**
     * Create a standard crash report window.
     * <p>A dark blue background, a game logo is positioned in the center at the top, with the report text below. Allow customization of the displayed content.
     *
     * @param crashReport the custom crash report, it is recommended to use {@link FakeCrashReportBuilder} for construction
     * @see com.smallmanseries.metalib.api.MetaCrash
     */
    public FakeCrashPanel(String crashReport) {
        TextArea textarea = new TextArea(crashReport, 0, 0, 1);
        textarea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        createPanel();
        add(textarea, "Center");
    }

    // Todo: Show image

    private void createPanel() {
        setBackground(new Color(0x2e3444));
        setLayout(new BorderLayout());
        add(new LogoCanvas(), "North");
        add(new CrashReportCanvas(80), "East");
        add(new CrashReportCanvas(80), "West");
        add(new CrashReportCanvas(100), "South");
    }

    static class CrashReportCanvas extends Canvas {

        public CrashReportCanvas(int size) {
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }
    }

    static class LogoCanvas extends Canvas {

        public LogoCanvas() {
            try {
                logo = ImageIO.read((CrashReport.class).getResource("/assets/minecraft/textures/gui/title/minecraft.png"));
            } catch (IOException ioexception) {
            }
            byte size = 100;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        public void paint(Graphics graphics) {
            super.paint(graphics);
            graphics.drawImage(logo, getWidth() / 2 - 137, 32, 274, 69, null);
        }

        private BufferedImage logo;
    }
}
