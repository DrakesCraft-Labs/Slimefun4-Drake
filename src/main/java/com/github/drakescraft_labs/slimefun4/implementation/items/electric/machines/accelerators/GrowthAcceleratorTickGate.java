package com.github.drakescraft_labs.slimefun4.implementation.items.electric.machines.accelerators;

final class GrowthAcceleratorTickGate {

    private GrowthAcceleratorTickGate() {
    }

    /**
     * Assigns each machine a deterministic phase so loaded farms share their work
     * across server ticks instead of all scanning their area simultaneously.
     */
    static boolean shouldTick(long worldTime, int x, int y, int z, int interval) {
        int normalizedInterval = Math.max(1, interval);

        if (normalizedInterval == 1) {
            return true;
        }

        long locationHash = 73428767L * x ^ 912931L * y ^ 19349663L * z;
        long phase = Math.floorMod(locationHash, normalizedInterval);
        return Math.floorMod(worldTime, normalizedInterval) == phase;
    }

}
