package cl.jackstar.slimefun4.core.services;

import java.util.Arrays;

/**
 * Pure rolling-window limiter used by SFMaster. The returned history is safe to
 * persist in a Player PDC, so restarts never reset paid-pass limits.
 */
final class ClaimWindow {

    private ClaimWindow() {}

    static Result consume(long[] rawHistory, long now, long windowMillis, int maximum) {
        long[] history = rawHistory == null ? new long[0] : rawHistory;
        long cutoff = now - Math.max(1L, windowMillis);
        long[] active = Arrays.stream(history)
                .filter(value -> value > cutoff && value <= now)
                .toArray();
        if (active.length >= Math.max(1, maximum)) {
            return new Result(false, active);
        }
        long[] updated = Arrays.copyOf(active, active.length + 1);
        updated[active.length] = now;
        return new Result(true, updated);
    }

    record Result(boolean allowed, long[] history) {}
}
