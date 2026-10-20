package com.smallmanseries.metalib.client.window;

/**
 * A single animation unit. Can be applied to window position or size.
 * <p>
 * When {@code initialized} is {@code false}, it indicates that the initial value has not been captured yet - it will be captured when the animation actually starts.
 */
public class WindowAnimation {
    public enum Property {POSITION, SIZE, CURSOR}

    public enum Mode {TO, BY, HOLD, FIX}

    // animation information
    final Property property;
    final Mode mode;
    final int targetA, targetB;      // TO: target；BY: increment
    final long durationMs;
    final Easing easing;
    final long holdMs;               // additional waiting time after animation completion

    // runtime state
    boolean initialized = false;
    long startTime;
    int fromA, fromB;
    float lastEasing = 0;            // record the last easing progress for use in BY mode

    WindowAnimation(Property property, Mode mode, int targetA, int targetB,
                    long durationMs, Easing easing, long holdMs) {
        this.property = property;
        this.mode = mode;
        this.targetA = targetA;
        this.targetB = targetB;
        this.durationMs = Math.max(1, durationMs);
        this.easing = easing == null ? Easing.LINEAR : easing;
        this.holdMs = holdMs;
    }

    /**
     * Create an uninitialized copy for re-enqueuing during the loop.
     */
    WindowAnimation copy() {
        return new WindowAnimation(property, mode, targetA, targetB,
                durationMs, easing, holdMs);
    }

    /**
     * Capture the starting value (call when the animation actually starts).
     */
    void captureStart(int currentA, int currentB) {
        this.fromA = currentA;
        this.fromB = currentB;
        this.startTime = System.currentTimeMillis();
        this.initialized = true;
    }

    /**
     * Calculate the value at the current progress.
     *
     * @return [a, b]
     */
    int[] evaluate(int px, int py) {
        long elapsed = System.currentTimeMillis() - startTime;
        float t = Math.min(1f, (float) elapsed / durationMs);
        float e = easing.apply(t);

        int a, b;
        switch (mode) {
            case TO -> {
                a = (int) (fromA + (targetA - fromA) * e);
                b = (int) (fromB + (targetB - fromB) * e);
            }
            case BY -> {
                // Calculate a new origin point based on the previous progress and current coordinates
                // to handle the player's operation of dragging the window.
                // Preventing the window from being locked by animations.
                fromA = (int) Math.ceil(px - targetA * lastEasing);
                fromB = (int) Math.ceil(py - targetB * lastEasing);
                a = (int) (fromA + targetA * e);
                b = (int) (fromB + targetB * e);
                lastEasing = e;
            }
            default -> {
                a = fromA;
                b = fromB;
            }
        }

        return new int[]{a, b};
    }

    /**
     * Whether the animation is completed (including {@code HOLD})
     */
    boolean isComplete() {
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed >= durationMs + holdMs;
    }

    /**
     * Whether it is of the {@code HOLD} type (with no change in value)
     */
    boolean isHold() {
        return mode == Mode.HOLD;
    }
}
