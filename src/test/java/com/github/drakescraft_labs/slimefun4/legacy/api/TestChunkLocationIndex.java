package com.github.drakescraft_labs.slimefun4.legacy.api;

import java.util.Set;

import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestChunkLocationIndex {

    @Test
    void testLocationsAreIndexedByChunkAndRemoved() {
        ChunkLocationIndex index = new ChunkLocationIndex();
        Location first = new Location(null, 1, 64, 1);
        Location second = new Location(null, 32, 64, 1);

        index.add(first);
        index.add(second);

        Assertions.assertEquals(Set.of(first), index.get(0, 0));
        Assertions.assertEquals(Set.of(second), index.get(2, 0));

        index.remove(first);

        Assertions.assertTrue(index.get(0, 0).isEmpty());
        Assertions.assertEquals(Set.of(second), index.get(2, 0));
    }
}
