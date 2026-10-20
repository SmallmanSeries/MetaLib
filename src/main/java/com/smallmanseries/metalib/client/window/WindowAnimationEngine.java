package com.smallmanseries.metalib.client.window;

import com.smallmanseries.metalib.MetaLib;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Window animation engine.
 * <p>
 * It operates independently of the Minecraft main body and utilizes a daemon thread to update every 16ms (one tick),
 * with a default tick rate of approximately 60tps.
 * <p>
 * Features:
 * <li> Execute animation queues sequentially to avoid absolute coordinate conflicts
 * <li> Support loop animation for continuous movement/scaling
 * <li> Support pause/resume
 * <li> Support batch cancellation by tag
 *
 * @author SmallmanSeries
 * @see WindowAnimationBuilder
 * @see com.smallmanseries.metalib.api.MetaWindowAnimate External API, providing commonly used presets/more examples
 * @since 1.0
 */
public final class WindowAnimationEngine {
    public static final WindowAnimationEngine INSTANCE = new WindowAnimationEngine();

    // ── Activity Instance ──────────────────────────────
    private final List<AnimationSequence> sequences = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong();

    // ── Global State ──────────────────────────────
    private volatile boolean globallyPaused = false;
    private final boolean autoPauseOnFullscreen = true;

    /**
     * Called by the daemon thread once every 16ms, which is approximately 60 tps.
     */
    public void tick() {
        if (sequences.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();

        // auto pause in full screen mode
        if (autoPauseOnFullscreen && mc.getWindow().isFullscreen()) return;
        if (globallyPaused) return;

        long handle = mc.getWindow().handle();

        // iterate over a copy to avoid deleting during iteration
        List<AnimationSequence> snapshot = new ArrayList<>(sequences);
        for (AnimationSequence seq : snapshot) {
            seq.tick(handle);
            if (seq.builder.onTick != null) {
                try {
                    seq.builder.onTick.run();
                } catch (Throwable t) {
                    MetaLib.LOGGER.error("onTick failed", t);
                }
            }
            if (seq.isFinished()) {
                sequences.remove(seq);
                if (seq.builder.onFinish != null) {
                    try {
                        seq.builder.onFinish.run();
                    } catch (Throwable t) {
                        MetaLib.LOGGER.error("onFinish failed", t);
                    }
                }
            }
        }
    }

    // ── Submit ──────────────────────────────────

    AnimationHandle submit(WindowAnimationBuilder builder) {
        AnimationSequence seq = new AnimationSequence(idGen.incrementAndGet(), builder);
        sequences.add(seq);
        return new AnimationHandle(seq);
    }

    // ── Global Control ──────────────────────────────

    public void pauseAll() {
        globallyPaused = true;
    }

    public void resumeAll() {
        globallyPaused = false;
    }

    public boolean isPaused() {
        return globallyPaused;
    }

    public void stopAll() {
        sequences.clear();
    }

    public void stopByTag(String tag) {
        sequences.removeIf(s -> tag.equals(s.builder.tag));
    }

    public boolean isAnimating() {
        return !sequences.isEmpty();
    }

    public boolean isAnimating(String tag) {
        return sequences.stream().anyMatch(s -> tag.equals(s.builder.tag));
    }

    // ── Daemon Thread ───────────────────────────────

    public static void startFrameDriver() {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Minecraft.getInstance().execute(INSTANCE::tick);
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }, "MetaLib-WindowAnim");
        t.setDaemon(true);
        t.start();
    }

    // ── Handle ──────────────────────────────────

    public static final class AnimationHandle {
        private final AnimationSequence seq;

        AnimationHandle(AnimationSequence seq) {
            this.seq = seq;
        }

        public void cancel() {
            seq.cancelled = true;
            INSTANCE.sequences.remove(seq);
        }

        public void pause() {
            seq.paused = true;
        }

        public void resume() {
            seq.paused = false;
        }

        public boolean isDone() {
            return seq.isFinished();
        }

        public long id() {
            return seq.id;
        }
    }

    // ── Internal: Sequence ────────────────────────────

    private static final class AnimationSequence {
        final long id;
        final WindowAnimationBuilder builder;
        final Queue<WindowAnimation> queue = new LinkedList<>();
        WindowAnimation current;
        boolean cancelled = false;
        boolean paused = false;

        AnimationSequence(long id, WindowAnimationBuilder builder) {
            this.id = id;
            this.builder = builder;
            queue.addAll(builder.steps);
        }

        void tick(long handle) {
            if (cancelled || paused) return;

            // next animation
            if (current == null) {
                current = queue.poll();

                if (current == null && builder.loop) {
                    // queue execution completed
                    for (WindowAnimation anim : builder.steps) {
                        queue.add(anim.copy());
                    }
                    current = queue.poll();
                }

                if (current == null) return;
            }

            int[] px = new int[1], py = new int[1];
            int[] sw = new int[1], sh = new int[1];
            double[] cx = new double[1], cy = new double[1];

            // three scenarios
            switch (current.property) {
                case POSITION -> {

                    // get the current parameters, the same below
                    GLFW.glfwGetWindowPos(handle, px, py);

                    // initialize, the same below
                    if (!current.initialized) {
                        current.captureStart(px[0], py[0]);
                    }

                    // calculate and apply easing, the same below
                    if (!current.isHold()) {
                        int[] values = current.evaluate(px[0], py[0]);
                        GLFW.glfwSetWindowPos(handle, values[0], values[1]);
                    }
                }
                case SIZE -> {
                    GLFW.glfwGetWindowSize(handle, sw, sh);
                    if (!current.initialized) {
                        current.captureStart(sw[0], sh[0]);
                    }
                    if (!current.isHold()) {
                        int[] values = current.evaluate(sw[0], sh[0]);
                        GLFW.glfwSetWindowSize(handle, values[0], values[1]);
                    }
                }
                case CURSOR -> {
                    GLFW.glfwGetCursorPos(handle, cx, cy);
                    if (!current.initialized) {
                        current.captureStart((int) cx[0], (int) cy[0]);
                    }
                    if (!current.isHold()) {
                        int[] values = current.evaluate((int) cx[0], (int) cy[0]);
                        GLFW.glfwSetCursorPos(handle, values[0], values[1]);
                    }
                }
            }

            // complete to advance
            if (current.isComplete()) {
                current = null;
            }
        }

        boolean isFinished() {
            return cancelled || (current == null && queue.isEmpty() && !builder.loop);
        }
    }
}
