package com.github.drakescraft_labs.slimefun4.core.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClaimWindowTest {

    @Test
    void persistsActiveClaimsAndRejectsTheNextOne() {
        long now = 1_000_000L;
        long[] history = {now - 30_000L, now - 20_000L};

        ClaimWindow.Result result = ClaimWindow.consume(history, now, 60_000L, 2);

        assertFalse(result.allowed());
        assertArrayEquals(history, result.history());
    }

    @Test
    void removesExpiredClaimsBeforeConsuming() {
        long now = 1_000_000L;

        ClaimWindow.Result result = ClaimWindow.consume(
                new long[] {now - 60_001L, now - 10_000L}, now, 60_000L, 2);

        assertTrue(result.allowed());
        assertArrayEquals(new long[] {now - 10_000L, now}, result.history());
    }

    @Test
    void rejectsFutureTimestampsInsteadOfTrustingCorruptData() {
        long now = 1_000_000L;

        ClaimWindow.Result result = ClaimWindow.consume(
                new long[] {now + 10_000L}, now, 60_000L, 1);

        assertTrue(result.allowed());
        assertArrayEquals(new long[] {now}, result.history());
    }
}
