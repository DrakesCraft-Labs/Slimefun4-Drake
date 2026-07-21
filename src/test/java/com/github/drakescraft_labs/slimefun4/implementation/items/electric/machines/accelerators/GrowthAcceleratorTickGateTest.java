package com.github.drakescraft_labs.slimefun4.implementation.items.electric.machines.accelerators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GrowthAcceleratorTickGateTest {

    @Test
    void intervalOneDoesNotThrottleMachines() {
        for (long tick = 0; tick < 20; tick++) {
            assertTrue(GrowthAcceleratorTickGate.shouldTick(tick, -42, 64, 9001, 1));
        }
    }

    @Test
    void machineRunsExactlyOncePerIntervalWindow() {
        int interval = 4;
        int runs = 0;

        for (long tick = 100; tick < 100 + interval; tick++) {
            if (GrowthAcceleratorTickGate.shouldTick(tick, -42, 64, 9001, interval)) {
                runs++;
            }
        }

        assertEquals(1, runs);
    }

}
