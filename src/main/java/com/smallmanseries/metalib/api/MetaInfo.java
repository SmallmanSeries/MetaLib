package com.smallmanseries.metalib.api;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * Classic Meta game operation: obtaining player information.
 * <p>
 * This class provides operations such as getting the player's system username, system time, IP address, operating system name, etc.
 * All methods should be called on the client side to synchronize with the player's own information.
 */
public final class MetaInfo {

    /**
     * Get the username of the operating system currently running the game instance.
     * Call this on the client side to retrieve the player's system username, in order to achieve the effect of breaking the fourth wall.
     *
     * @return a {@link Component} that contains the system username
     */
    public static Component getSystemUsername() {
        return Component.literal(System.getProperty("user.name"));
    }

    /**
     * Get the system time of the operating system currently running the game instance.
     * Call this on the client side to retrieve the player's system time.
     *
     * @param format a string used for formatting system time
     * @return a {@link Component} that contains the formatted system time
     */
    public static Component getSystemTime(String format) {
        return Component.literal(LocalDateTime.now().format(DateTimeFormatter.ofPattern(format)));
    }

    /**
     * Attempt to get the IP address of the computer currently running the game instance.
     *
     * @return a {@link Component} that contains the IP address when succeeded. <code>null</code> when failed.
     */
    public static @Nullable Component getLocalIPAddress() {
        try {
            // Try to traverse all interfaces
            Enumeration<NetworkInterface> iFaces = NetworkInterface.getNetworkInterfaces();
            while (iFaces.hasMoreElements()) {
                NetworkInterface iFace = iFaces.nextElement();
                if (iFace.isLoopback() || !iFace.isUp()) continue;
                Enumeration<InetAddress> addrs = iFace.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return Component.literal(addr.getHostAddress());
                    }
                }
            }
            // fallback: direct access
            return Component.literal(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the name and version of the operating system currently running the game instance.
     * Call this on the client side to retrieve the player's system name and version.
     *
     * @return a {@link Component} that contains the system name and version
     */
    public static Component getOSName() {
        return Component.literal(System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }

    //Todo hardware information
}
