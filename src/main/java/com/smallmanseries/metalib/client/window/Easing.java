package com.smallmanseries.metalib.client.window;

/**
 * Easing function. Input parameter t ∈ [0,1], output parameter is the interpolation ratio.
 * <p>
 * Provides a variety of commonly used curves, covering effects such as "smooth", "elastic", "bounce", and "step".
 */
public enum Easing {
    LINEAR(t -> t),

    // quadratic
    EASE_IN(t -> t * t),
    EASE_OUT(t -> 1 - (1 - t) * (1 - t)),
    EASE_IN_OUT(t -> t < 0.5f ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2),

    // cubic
    EASE_IN_CUBIC(t -> t * t * t),
    EASE_OUT_CUBIC(t -> 1 - (float) Math.pow(1 - t, 3)),
    EASE_IN_OUT_CUBIC(t -> t < 0.5f
            ? 4 * t * t * t
            : 1 - (float) Math.pow(-2 * t + 2, 3) / 2),

    // exponential
    EASE_IN_EXPO(t -> t == 0 ? 0 : (float) Math.pow(2, 10 * t - 10)),
    EASE_OUT_EXPO(t -> t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t)),

    // sine
    EASE_IN_OUT_SINE(t -> -(float) (Math.cos(Math.PI * t) - 1) / 2),

    // rebound
    EASE_OUT_BACK(t -> {
        float c1 = 1.70158f, c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }),

    // elastic
    EASE_OUT_ELASTIC(t -> {
        if (t == 0 || t == 1) return t;
        float c4 = (float) (2 * Math.PI) / 3;
        return (float) Math.pow(2, -10 * t) * (float) Math.sin((t * 10 - 0.75) * c4) + 1;
    }),

    // bounce
    EASE_OUT_BOUNCE(t -> {
        float n1 = 7.5625f, d1 = 2.75f;
        if (t < 1 / d1) return n1 * t * t;
        if (t < 2 / d1) return n1 * (t -= 1.5f / d1) * t + 0.75f;
        if (t < 2.5f / d1) return n1 * (t -= 2.25f / d1) * t + 0.9375f;
        return n1 * (t -= 2.625f / d1) * t + 0.984375f;
    }),

    // stepped (No interpolation, for "teleportation")
    STEP(t -> t < 1f ? 0f : 1f);

    // jitter (each step change, for "glitch")
    // JITTER(t -> t);

    private final EasingFunction fn;

    Easing(EasingFunction fn) {
        this.fn = fn;
    }

    public float apply(float t) {
        return fn.apply(Math.clamp(t, 0f, 1f));
    }

    @FunctionalInterface
    public interface EasingFunction {
        float apply(float t);
    }
}
