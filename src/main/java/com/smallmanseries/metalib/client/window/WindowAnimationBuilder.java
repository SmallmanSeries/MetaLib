package com.smallmanseries.metalib.client.window;

import java.util.ArrayList;
import java.util.List;

/**
 * Build window animation sequences in a chained manner.
 * <p>
 * Example:
 * {@snippet lang = java:
 *      * MetaWindowAPI.animate()
 *      * .moveTo(100, 100, 1000, Easing.EASE_IN_OUT_CUBIC)
 *      * .scaleTo(800, 600, 500)
 *      * .hold(200)
 *      * .moveBy(50, 0, 200)
 *      * .loop()
 *      * .start();
 *}
 *
 * @author SmallmanSeries
 * @see WindowAnimationEngine
 * @see com.smallmanseries.metalib.api.MetaWindowAnimate External API, providing commonly used presets/more examples
 * @since 1.0
 */
public class WindowAnimationBuilder {
    final List<WindowAnimation> steps = new ArrayList<>();
    boolean loop = false;
    boolean cursor = false;
    Runnable onFinish;
    String tag = "default";

    // ── Position ──────────────────────────────────
    public WindowAnimationBuilder moveTo(int x, int y, long ms) {
        return moveTo(x, y, ms, Easing.LINEAR);
    }

    public WindowAnimationBuilder moveTo(int x, int y, long ms, Easing easing) {
        steps.add(new WindowAnimation(cursor ? WindowAnimation.Property.CURSOR : WindowAnimation.Property.POSITION,
                WindowAnimation.Mode.TO, x, y, ms, easing, 0));
        return this;
    }

    public WindowAnimationBuilder moveBy(int dx, int dy, long ms) {
        return moveBy(dx, dy, ms, Easing.LINEAR);
    }

    public WindowAnimationBuilder moveBy(int dx, int dy, long ms, Easing easing) {
        steps.add(new WindowAnimation(cursor ? WindowAnimation.Property.CURSOR : WindowAnimation.Property.POSITION,
                WindowAnimation.Mode.BY, dx, dy, ms, easing, 0));
        return this;
    }

    // ── Size ──────────────────────────────────
    public WindowAnimationBuilder scaleTo(int w, int h, long ms) {
        return scaleTo(w, h, ms, Easing.LINEAR);
    }

    public WindowAnimationBuilder scaleTo(int w, int h, long ms, Easing easing) {
        steps.add(new WindowAnimation(WindowAnimation.Property.SIZE,
                WindowAnimation.Mode.TO, w, h, ms, easing, 0));
        return this;
    }

    public WindowAnimationBuilder scaleBy(int dw, int dh, long ms) {
        return scaleBy(dw, dh, ms, Easing.LINEAR);
    }

    public WindowAnimationBuilder scaleBy(int dw, int dh, long ms, Easing easing) {
        steps.add(new WindowAnimation(WindowAnimation.Property.SIZE,
                WindowAnimation.Mode.BY, dw, dh, ms, easing, 0));
        return this;
    }

    // ── Wait ──────────────────────────────────
    public WindowAnimationBuilder hold(long ms) {
        steps.add(new WindowAnimation(cursor ? WindowAnimation.Property.CURSOR : WindowAnimation.Property.POSITION,
                WindowAnimation.Mode.HOLD, 0, 0, 1, Easing.LINEAR, ms));
        return this;
    }

    // ── Fixing Window ─────────────────────────
    public WindowAnimationBuilder fix(long ms) {
        steps.add(new WindowAnimation(cursor ? WindowAnimation.Property.CURSOR : WindowAnimation.Property.POSITION,
                WindowAnimation.Mode.FIX, 0, 0, 1, Easing.LINEAR, ms));
        return this;
    }

    // ── Jitter / Glitch Preset ────────────────

    /**
     * Add a jitter: rapidly oscillate several times between (x±range, y±range)
     */
    public WindowAnimationBuilder jitter(int range, int times, long totalMs) {
        int stepMs = (int) Math.max(10, totalMs / times);
        for (int i = 0; i < times; i++) {
            int dx = (int) (Math.random() * range * 2) - range;
            int dy = (int) (Math.random() * range * 2) - range;
            moveBy(dx, dy, stepMs / 2, Easing.LINEAR);
            moveBy(-dx, -dy, stepMs / 2, Easing.LINEAR);
        }
        return this;
    }

    // ── Loop and Callback ─────────────────────
    public WindowAnimationBuilder loop() {
        this.loop = true;
        return this;
    }

    public WindowAnimationBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public WindowAnimationBuilder onFinish(Runnable r) {
        this.onFinish = r;
        return this;
    }

    /**
     * Whether it is a cursor animation.
     */
    public WindowAnimationBuilder cursor() {
        this.cursor = true;
        return this;
    }

    /**
     * Submit to the animation engine ({@link WindowAnimationEngine}).
     */
    public WindowAnimationEngine.AnimationHandle start() {
        return WindowAnimationEngine.INSTANCE.submit(this);
    }
}
