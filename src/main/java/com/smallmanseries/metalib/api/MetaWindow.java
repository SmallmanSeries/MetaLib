package com.smallmanseries.metalib.api;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * Classic Meta game operation: window animations
 * <p>
 * This class provides operations to modify the player's game window,
 * such as moving the window, resizing the window, toggle fullscreen, setting the window title, and moving the mouse cursor.
 * <p>
 * All methods should be called on the client side.
 *
 * @author KiTheAdventurer
 * @since 1.0
 */
public class MetaWindow {

    private static long handle() {
        return Minecraft.getInstance().getWindow().handle();
    }

    /**
     * Check whether the player's game is in full-screen mode.
     * @return boolean value, indicating whether the game is in fullscreen mode
     */
    public static boolean isFullscreen() {
        return Minecraft.getInstance().getWindow().isFullscreen();
    }

    /**
     * Set the game to full-screen mode. If it is already in full-screen mode, ignore this operation.
     */
    public static void setFullscreen() {
        if (isFullscreen()) return;
        Minecraft.getInstance().getWindow().toggleFullScreen();
    }

    /**
     * Set the game to windowed mode and specify the window size. If it is already in windowed mode, reset the window size.
     * <p>
     * The window will be scaled with the origin (top left corner) as the axis.
     * @param w window width
     * @param h window height
     */
    public static void setWindowed(int w, int h) {
        Minecraft.getInstance().getWindow().setWindowed(w, h);
    }

    /**
     * Set the size of the game window. If it is already in full-screen mode, ignore this operation.
     * <p>
     * The window will be scaled with the origin (top left corner) as the axis.
     * @param w window width
     * @param h window height
     */
    public static void setWindowSize(int w, int h) {
        if (isFullscreen()) return;
        Minecraft.getInstance().execute(() -> GLFW.glfwSetWindowSize(handle(), w, h));
    }

    /**
     * Perform relative scaling on the window, that is, increase or decrease the size of the current window.
     * <p>
     * A positive increment means expanding the window, while a negative increment means shrinking the window.
     * <p>
     * The window will be scaled with the origin (top left corner) as the axis.
     * @param dw the increment of window width
     * @param dh the increment of window height
     */
    public static void scaleWindow(int dw, int dh) {
        int[] cw = new int[1], ch = new int[1];
        GLFW.glfwGetWindowSize(handle(), cw, ch);
        setWindowSize(cw[0] + dw, ch[0] + dh);
    }

    /**
     * Set the position of the game window, that is, set the coordinates of the window origin (top left corner) on the screen.
     * @param x the x-coordinate of the window origin
     * @param y the y-coordinate of the window origin
     */
    public static void setWindowPos(int x, int y) {
        if (isFullscreen()) return;
        Minecraft.getInstance().execute(() -> GLFW.glfwSetWindowPos(handle(), x, y));
    }

    /**
     * Move the game window, that is, relatively move the window a certain distance on the screen.
     * @param dx the movement distance of the x-coordinate. A positive value indicates a rightward movement, while a negative value indicates a leftward movement
     * @param dy the movement distance of the y-coordinate. A positive value indicates a downward movement, while a negative value indicates an upward movement
     */
    public static void moveWindow(int dx, int dy) {
        int[] cx = new int[1], cy = new int[1];
        GLFW.glfwGetWindowPos(handle(), cx, cy);
        setWindowPos(cx[0] + dx, cy[0] + dy);
    }

    /**
     * Set the title of the game window.
     * <p>It should be noted that the vanilla Minecraft may frequently update the window title (usually when the player opens certain interfaces), so the title may be quickly changed back after being modified here, which is unstable.
     * <p>You can avoid this situation by modifying {@code updateTitle()} in {@link Minecraft}.
     * @param title new title of the window
     */
    public static void setWindowTitle(String title) {
        Minecraft.getInstance().execute(() ->
                GLFW.glfwSetWindowTitle(handle(), title));
    }

    /**
     * Set the position of the player's mouse cursor, that is, set the coordinates of the cursor on the screen.
     * @param x the x-coordinate of the cursor
     * @param y the y-coordinate of the cursor
     */
    public static void setCursorPos(int x, int y) {
        Minecraft.getInstance().execute(() -> GLFW.glfwSetCursorPos(handle(), x, y));
    }

    /**
     * Move the player's mouse pointer, that is, relatively move the pointer a certain distance on the screen.
     * @param dx the movement distance of the x-coordinate. A positive value indicates a rightward movement, while a negative value indicates a leftward movement
     * @param dy the movement distance of the y-coordinate. A positive value indicates a downward movement, while a negative value indicates an upward movement
     */
    public static void moveCursor(int dx, int dy) {
        double[] cx = new double[1], cy = new double[1];
        GLFW.glfwGetCursorPos(handle(), cx, cy);
        setCursorPos((int) (cx[0] + dx), (int) (cy[0] + dy));
    }

    /**
     * Get the position and size information of the game window.
     * @return an array, with the contents being {@code x}, {@code y}, {@code width}, and {@code height} in subscript order.
     */
    public static int[] getWindowBounds() {
        int[] x = new int[1], y = new int[1], w = new int[1], h = new int[1];
        GLFW.glfwGetWindowPos(handle(), x, y);
        GLFW.glfwGetWindowSize(handle(), w, h);
        return new int[]{x[0], y[0], w[0], h[0]};
    }

    /**
     * Get the position information of the player's mouse cursor.
     * @return an array, with the contents being {@code x}, {@code y} in subscript order.
     */
    public static double[] getCursorPos() {
        double[] x = new double[1], y = new double[1];
        GLFW.glfwGetCursorPos(handle(), x, y);
        return new double[]{x[0], y[0]};
    }
}
