package com.github.drakescraft_labs.slimefun4.legacy.api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.bukkit.Location;

/** Maintains an O(1) chunk index for persisted Slimefun block locations. */
final class ChunkLocationIndex {

    private final Map<Long, Set<Location>> locations = new ConcurrentHashMap<>();

    void add(@Nonnull Location location) {
        long key = key(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        locations.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet()).add(location);
    }

    void remove(@Nonnull Location location) {
        long key = key(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        locations.computeIfPresent(key, (ignored, indexedLocations) -> {
            indexedLocations.remove(location);
            return indexedLocations.isEmpty() ? null : indexedLocations;
        });
    }

    @Nonnull
    Set<Location> get(int chunkX, int chunkZ) {
        Set<Location> indexedLocations = locations.get(key(chunkX, chunkZ));
        return indexedLocations == null ? Set.of() : Set.copyOf(indexedLocations);
    }

    private static long key(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) ^ (chunkZ & 0xffffffffL);
    }
}
