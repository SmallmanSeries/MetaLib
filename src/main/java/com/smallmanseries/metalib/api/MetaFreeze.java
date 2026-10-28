package com.smallmanseries.metalib.api;

import net.minecraft.client.Minecraft;

/**
 * Classic Meta game operation: freeze or lag.
 * <p>
 * This class provides some operations for causing the game to freeze or lag.
 * All methods should be called on the client side.
 *
 * @author SmallmanSeries
 * @since 1.0
 */
public final class MetaFreeze {
    private MetaFreeze() {
    }

    /**
     * Freeze the game for a specified duration.
     * @param durationMs freeze time, measured in milliseconds
     */
    public static void freezeGame(int durationMs) {
        Minecraft.getInstance().execute(() -> {
            try {
                Thread.sleep(durationMs);
            } catch (InterruptedException ignored) {
            }
        });
    }

    /**
     * Cause the game to stutter
     * @param durationMs stutter time within one cycle, measured in milliseconds
     * @param intervalMs the interval between cycles, measured in milliseconds
     * @param times number of stutters
     */
    public static void stutterGame(int durationMs, int intervalMs, int times) {
        new Thread(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    freezeGame(durationMs);
                    Thread.sleep(intervalMs);
                }
            } catch (InterruptedException ignored) {
            }
        }, "MetaLib-Stutter").start();
    }
}