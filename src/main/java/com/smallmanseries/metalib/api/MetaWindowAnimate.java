package com.smallmanseries.metalib.api;

import com.smallmanseries.metalib.client.window.Easing;
import com.smallmanseries.metalib.client.window.WindowAnimationBuilder;
import com.smallmanseries.metalib.client.window.WindowAnimationEngine;

/**
 * Window animation API: Utilizes the MetaLib animation engine, offering a range of commonly used animation presets.
 * <p>
 * All methods should be called on the client side.
 *
 * @author SmallmanSeries
 * @see WindowAnimationBuilder
 * @see WindowAnimationEngine
 * @since 1.0
 */
public class MetaWindowAnimate {
    private MetaWindowAnimate() {
    }

    /**
     * Returns Builder, chain-build the animation sequence.
     */
    public static WindowAnimationBuilder animate() {
        return new WindowAnimationBuilder();
    }

// ── Commonly Used Presets ──────────────────────────────

    /**
     * Smoothly move to the target position within durationMs.
     * <p>
     * Example: Move to (100, 100) within one second
     */
    public static WindowAnimationEngine.AnimationHandle moveTo(int x, int y, long durationMs, Easing easing) {
        return animate().moveTo(x, y, durationMs, easing).start();
    }

    /**
     * Smooth zoom window.
     */
    public static WindowAnimationEngine.AnimationHandle scaleTo(int w, int h, long durationMs, Easing easing) {
        return animate().scaleTo(w, h, durationMs, easing).start();
    }

    /**
     * Smooth movement (relative position).
     */
    public static WindowAnimationEngine.AnimationHandle moveBy(int dx, int dy, long durationMs, Easing easing) {
        return animate().moveBy(dx, dy, durationMs, easing).start();
    }

    /**
     * Smooth zoom window (relative size).
     */
    public static WindowAnimationEngine.AnimationHandle scaleBy(int dw, int dh, long durationMs, Easing easing) {
        return animate().scaleBy(dw, dh, durationMs, easing).start();
    }

    /**
     * Continuous drift: The window moves a certain distance towards (dx, dy) every periodMs, in an infinite loop.
     * <p>
     * Visually, it appears as if it is being slowly dragged away by a hand.
     */
    public static WindowAnimationEngine.AnimationHandle startDrift(int dx, int dy, long periodMs, Easing easing) {
        return animate()
                .moveBy(dx, dy, periodMs, easing)
                .loop()
                .tag("drift")
                .start();
    }

    /**
     * Continuous zooming: The window zooms by (dx, dy) every periodMs, in an infinite loop.
     */
    public static WindowAnimationEngine.AnimationHandle startScale(int dw, int dh, long periodMs, Easing easing) {
        return animate()
                .scaleBy(dw, dh, periodMs, easing)
                .loop()
                .tag("scale")
                .start();
    }

    /**
     * Fix window (cannot be dragged by the player).
     */
    public static WindowAnimationEngine.AnimationHandle fix(long durationMs) {
        return animate().fix(durationMs).start();
    }

    /**
     * Move the cursor to the designated position, and do not allow the player to control the cursor.
     */
    public static WindowAnimationEngine.AnimationHandle moveCursorTo(int x, int y, long durationMs, Easing easing) {
        return animate().cursor().moveTo(x, y, durationMs, easing).start();
    }

    /**
     * Move the cursor to the relative position, allowing the player to control the cursor.
     */
    public static WindowAnimationEngine.AnimationHandle moveCursorBy(int x, int y, long durationMs, Easing easing) {
        return animate().cursor().moveBy(x, y, durationMs, easing).start();
    }

    /**
     * Cursor drift.
     */
    public static WindowAnimationEngine.AnimationHandle startCursorDrift(int dx, int dy, long periodMs, Easing easing) {
        return animate()
                .cursor()
                .moveBy(dx, dy, periodMs, easing)
                .loop()
                .tag("drift_cursor")
                .start();
    }

    /**
     * Fix the cursor.
     */
    public static WindowAnimationEngine.AnimationHandle fixCursor(long durationMs) {
        return animate().cursor().fix(durationMs).start();
    }

    /**
     * Window jitter preset.
     */
    public static WindowAnimationEngine.AnimationHandle shakeWindow(long durationMs, int intensity) {
        int[] bounds = MetaWindow.getWindowBounds();
        return animate()
                .jitter(intensity, (int) (durationMs / 30), durationMs)
                .moveTo(bounds[0], bounds[1], 30) // 保险：抖动动画结束后窗口回到原位
                .start();
    }

    /**
     * Cursor jitter preset.
     */
    public static WindowAnimationEngine.AnimationHandle shakeCursor(long durationMs, int intensity) {
        double[] pos = MetaWindow.getCursorPos();
        return animate()
                .cursor()
                .jitter(intensity, (int) (durationMs / 30), durationMs)
                .moveTo((int) pos[0], (int) pos[1], 30) // 保险：抖动动画结束后窗口回到原位
                .start();
    }

    /**
     * Continuous oscillation: The window oscillates back and forth within the range of (x±range, y±range).
     * <p>
     * Visually, it appears to be slightly shaken.
     */
    public static WindowAnimationEngine.AnimationHandle startOscillate(
            int range, long periodMs, boolean horizontal) {
        if (horizontal) {
            return animate()
                    .moveBy(range, 0, periodMs / 2, Easing.EASE_IN_OUT_SINE)
                    .moveBy(-range, 0, periodMs / 2, Easing.EASE_IN_OUT_SINE)
                    .loop()
                    .tag("oscillate")
                    .start();
        } else {
            return animate()
                    .moveBy(0, range, periodMs / 2, Easing.EASE_IN_OUT_SINE)
                    .moveBy(0, -range, periodMs / 2, Easing.EASE_IN_OUT_SINE)
                    .loop()
                    .tag("oscillate")
                    .start();
        }
    }

    /**
     * Continuous zoom pulse: The window zooms back and forth between (minW, minH) and (maxW, maxH).
     */
    public static WindowAnimationEngine.AnimationHandle startPulseScale(
            int minW, int minH, int maxW, int maxH, long periodMs) {
        return animate()
                .scaleTo(maxW, maxH, periodMs / 2, Easing.EASE_IN_OUT_SINE)
                .scaleTo(minW, minH, periodMs / 2, Easing.EASE_IN_OUT_SINE)
                .loop()
                .tag("pulse")
                .start();
    }

    /**
     * Breathing effect: The window slowly sways left and right near its current position + size pulse.
     */
    public static WindowAnimationEngine.AnimationHandle startBreathing(int moveRange, int sizeRange,
                                                                       long periodMs) {
        return animate()
                .moveBy(moveRange, 0, periodMs / 4, Easing.EASE_IN_OUT_SINE)
                .scaleBy(sizeRange, sizeRange, periodMs / 4, Easing.EASE_IN_OUT_SINE)
                .moveBy(-moveRange, 0, periodMs / 4, Easing.EASE_IN_OUT_SINE)
                .scaleBy(-sizeRange, -sizeRange, periodMs / 4, Easing.EASE_IN_OUT_SINE)
                .loop()
                .tag("breathing")
                .start();
    }

// ── Global Control ──────────────────────────────

    public static void stopAllAnimations() {
        WindowAnimationEngine.INSTANCE.stopAll();
    }

    public static void stopAnimations(String tag) {
        WindowAnimationEngine.INSTANCE.stopByTag(tag);
    }

    public static void pauseAnimations() {
        WindowAnimationEngine.INSTANCE.pauseAll();
    }

    public static void resumeAnimations() {
        WindowAnimationEngine.INSTANCE.resumeAll();
    }

    public static boolean isAnimating() {
        return WindowAnimationEngine.INSTANCE.isAnimating();
    }

    public static boolean isAnimating(String tag) {
        return WindowAnimationEngine.INSTANCE.isAnimating(tag);
    }
}

