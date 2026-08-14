package com.github.drakescraft_labs.slimefun4.implementation.tasks.armor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TestSlimefunArmorRefresh {

    @Test
    void keepsHealthyEffectWithoutCyclingIt() {
        assertFalse(SlimefunArmorTask.shouldRefresh(1, 400, 1));
    }

    @Test
    void refreshesEffectNearExpiration() {
        assertTrue(SlimefunArmorTask.shouldRefresh(1, 60, 1));
    }

    @Test
    void upgradesWeakerEffectImmediately() {
        assertTrue(SlimefunArmorTask.shouldRefresh(0, 400, 1));
    }
}
