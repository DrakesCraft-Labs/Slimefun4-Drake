package com.github.drakescraft_labs.slimefun4.utils;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestBlockStorageIntegrity {

    @Test
    void testExactAndWallVariants() {
        assertTrue(BlockStorageIntegrity.matches(Material.MAGENTA_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS));
        assertTrue(BlockStorageIntegrity.matches(Material.PLAYER_WALL_HEAD, Material.PLAYER_HEAD));
        assertTrue(BlockStorageIntegrity.matches(Material.OAK_WALL_SIGN, Material.OAK_SIGN));
        assertTrue(BlockStorageIntegrity.matches(Material.WALL_TORCH, Material.TORCH));
    }

    @Test
    void testCauldronVariants() {
        assertTrue(BlockStorageIntegrity.matches(Material.WATER_CAULDRON, Material.CAULDRON));
        assertTrue(BlockStorageIntegrity.matches(Material.CAULDRON, Material.WATER_CAULDRON));
        assertTrue(BlockStorageIntegrity.matches(Material.LAVA_CAULDRON, Material.CAULDRON));
        assertTrue(BlockStorageIntegrity.matches(Material.POWDER_SNOW_CAULDRON, Material.CAULDRON));
        assertFalse(BlockStorageIntegrity.matches(Material.DIRT, Material.CAULDRON));
    }

    @Test
    void testRejectsGhostMetadataOnVanillaBlocks() {
        assertFalse(BlockStorageIntegrity.matches(Material.DIRT, Material.MAGENTA_STAINED_GLASS));
        assertFalse(BlockStorageIntegrity.matches(Material.SNOW, Material.HOPPER));
        assertFalse(BlockStorageIntegrity.matches(Material.AIR, Material.DISPENSER));
    }
}
