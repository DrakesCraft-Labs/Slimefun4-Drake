package com.github.drakescraft_labs.slimefun4.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CheatDeliveryPathTest {

    @Test
    void categoryAndSearchUseTheSameDeliveryGate() throws IOException {
        String guide = Files.readString(Path.of(
                "src", "main", "java", "com", "github", "drakescraft_labs",
                "slimefun4", "implementation", "guide", "SurvivalSlimefunGuide.java"));

        assertEquals(2, occurrences(guide, "CheatPolicy.claim("));
        assertFalse(guide.contains("p.getInventory().addItem(sfItem.getItem()"));
    }

    @Test
    void productionDefaultsKeepThePublishedRollingLimit() throws IOException {
        String config = Files.readString(Path.of("src", "main", "resources", "config.yml"));

        assertTrue(config.contains("max-claims: 12"));
        assertTrue(config.contains("premium-max-claims: 48"));
        assertTrue(config.contains("window-minutes: 60"));
        assertTrue(config.contains("limited-permission: \"odysseia.sfmaster.active\""));
        assertTrue(config.contains("premium-limit-permission: \"odysseia.sfmaster.titan\""));
    }

    private static int occurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
