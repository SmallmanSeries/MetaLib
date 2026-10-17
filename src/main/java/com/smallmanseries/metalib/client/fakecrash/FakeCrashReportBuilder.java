package com.smallmanseries.metalib.client.fakecrash;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.SharedConstants;
import org.lwjgl.Version;
import org.spongepowered.include.com.google.common.base.Charsets;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create a crash report for {@link FakeCrashPanel} to use.
 */
public class FakeCrashReportBuilder {
    private static String RENDER_INFO;
    private String report = "";

    public static void getRenderInfo() {
        GpuDevice device = RenderSystem.getDevice();
        RENDER_INFO = device.getRenderer() + " version " + device.getVersion() + ", " + device.getVendor();
    }

    /**
     * [title]
     * <p>
     * ------
     */
    public FakeCrashReportBuilder title(String title) {
        report += "\n\n      " + title + "      \n      ";
        report += "-".repeat(title.getBytes(Charsets.US_ASCII).length);
        report += "      \n\n";
        return this;
    }

    /**
     * [description]
     */
    public FakeCrashReportBuilder description(String description) {
        report += description;
        report += "\n";
        return this;
    }

    /**
     * Minecraft has crashed!
     * <p>
     * ----------------------
     */
    public FakeCrashReportBuilder classicTitle() {
        report += "\n\n      Minecraft has crashed!      \n      ----------------------      \n\n";
        return this;
    }

    /**
     * Minecraft has stopped running because it encountered a problem.
     * <p>
     * <p>
     * If you wish to report this, please copy this entire text and email it to [email].
     * <p>
     * Please include a description of what you did when the error occured.
     */
    public FakeCrashReportBuilder classicDescription(String email) {
        report += "Minecraft has stopped running because it encountered a problem.\n\nIf you wish to report this, please copy this entire text and email it to " + email + ".\nPlease include a description of what you did when the error occured.\n";
        return this;
    }

    /**
     * Bad video card drivers!
     * <p>
     * -----------------------
     */
    public FakeCrashReportBuilder badVideoCardDriveTitle() {
        report += "\n\n      Bad video card drivers!      \n      -----------------------      \n\n";
        return this;
    }

    /**
     * Minecraft was unable to start because it failed to find an accelerated OpenGL mode.
     * <p>
     * This can usually be fixed by updating the video card drivers.
     * <p>
     * <p>
     * You might be able to find drivers for your video card here:
     * <p>
     * [url]
     */
    public FakeCrashReportBuilder badVideoCardDriveDescription(String url) {
        report += "Minecraft was unable to start because it failed to find an accelerated OpenGL mode.\nThis can usually be fixed by updating the video card drivers.\n\nYou might be able to find drivers for your video card here:\n  " + url;
        return this;
    }

    /**
     * --- BEGIN ERROR REPORT XXXXX --------
     */
    public FakeCrashReportBuilder begin() {
        report += "\n\n\n--- BEGIN ERROR REPORT " + Integer.toHexString(report.hashCode()) + " --------\n";
        return this;
    }

    /**
     * Generated XXXXX
     */
    public FakeCrashReportBuilder generated() {
        report += "Generated " + (new SimpleDateFormat()).format(new Date()) + "\n\n";
        return this;
    }

    /**
     * Minecraft: Minecraft XX.XX.XX
     * <p>
     * OS: XXXXX
     * <p>
     * Java: XXXXX
     * <p>
     * VM: XXXXX
     * <p>
     * LWJGL: XXXXX
     * <p>
     * OpenGL: XXXXX
     */
    public FakeCrashReportBuilder info() {
        try {
            report += "Minecraft: Minecraft " + SharedConstants.getCurrentVersion().name() + "\n"
                    + "OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version") + "\n"
                    + "Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor") + "\n"
                    + "VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor") + "\n"
                    + "LWJGL: " + Version.getVersion() + "\n"
                    + "OpenGL: " + RENDER_INFO + "\n\n";
        } catch (Throwable throwable) {
            report += "[failed to get system properties (" + throwable + ")]\n\n";
        }
        return this;
    }

    /**
     * --- END ERROR REPORT XXXXX --------
     */
    public FakeCrashReportBuilder end() {
        report += "\n--- END ERROR REPORT " + Integer.toHexString(report.hashCode()) + " ----------\n\n\n";
        return this;
    }

    public static String classicReport(String email, String stack) {
        return new FakeCrashReportBuilder()
                .classicTitle()
                .classicDescription(email)
                .begin()
                .generated()
                .info()
                .description(stack)
                .end()
                .build();
    }

    public static String classicGraphicsReport(String url, String stack) {
        return new FakeCrashReportBuilder()
                .badVideoCardDriveTitle()
                .badVideoCardDriveDescription(url)
                .begin()
                .generated()
                .info()
                .description(stack)
                .end()
                .build();
    }

    public String build() {
        return report;
    }
}
