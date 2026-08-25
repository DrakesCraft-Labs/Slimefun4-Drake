package com.github.drakescraft_labs.slimefun4.implementation.guide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestGuideBookmarks {

    @TempDir
    Path directory;

    @Test
    void testTogglePersistsItemIdsInInsertionOrder() {
        File file = directory.resolve("guide-bookmarks.yml").toFile();
        UUID playerId = UUID.randomUUID();
        GuideBookmarks bookmarks = new GuideBookmarks(file, Logger.getLogger("test"));

        assertTrue(bookmarks.toggle(playerId, "IRON_DUST"));
        assertTrue(bookmarks.toggle(playerId, "CARBONADO"));
        assertTrue(bookmarks.contains(playerId, "IRON_DUST"));
        assertEquals(2, bookmarks.size(playerId));

        GuideBookmarks reloaded = new GuideBookmarks(file, Logger.getLogger("test"));
        assertEquals(java.util.List.of("IRON_DUST", "CARBONADO"), reloaded.getBookmarks(playerId));
        assertFalse(reloaded.toggle(playerId, "IRON_DUST"));
        assertEquals(java.util.List.of("CARBONADO"), reloaded.getBookmarks(playerId));
    }

    @Test
    void testGroupBookmarksPersistIndependentlyFromItemBookmarks() {
        File file = directory.resolve("guide-bookmarks.yml").toFile();
        UUID playerId = UUID.randomUUID();
        GuideBookmarks bookmarks = new GuideBookmarks(file, Logger.getLogger("test"));

        assertTrue(bookmarks.toggle(playerId, "IRON_DUST"));
        assertTrue(bookmarks.toggleGroup(playerId, "drakescraft:infinity_recipes"));
        assertTrue(bookmarks.containsGroup(playerId, "drakescraft:infinity_recipes"));

        // Item and group favorites must not collide in storage.
        assertEquals(java.util.List.of("IRON_DUST"), bookmarks.getBookmarks(playerId));
        assertEquals(1, bookmarks.groupSize(playerId));

        GuideBookmarks reloaded = new GuideBookmarks(file, Logger.getLogger("test"));
        assertEquals(java.util.List.of("IRON_DUST"), reloaded.getBookmarks(playerId));
        assertEquals(java.util.List.of("drakescraft:infinity_recipes"), reloaded.getGroupBookmarks(playerId));

        assertFalse(reloaded.toggleGroup(playerId, "drakescraft:infinity_recipes"));
        assertEquals(0, reloaded.groupSize(playerId));
    }
}
