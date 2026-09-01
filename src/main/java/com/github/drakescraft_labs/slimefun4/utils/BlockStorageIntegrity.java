package com.github.drakescraft_labs.slimefun4.utils;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;

/** Validates that persisted Slimefun metadata still matches the physical block in the world. */
public final class BlockStorageIntegrity {

    private BlockStorageIntegrity() {}

    /**
     * Checks the normal placed material plus Bukkit's wall variants for heads, signs, banners
     * and torches. Metadata on any other physical material is an orphaned Slimefun block.
     */
    public static boolean matches(@Nonnull Material physical, @Nonnull Material expected) {
        if (physical == expected) {
            return true;
        }

        if (isCauldron(physical) && isCauldron(expected)) {
            return true;
        }

        String physicalName = physical.name();
        if (physicalName.startsWith("WALL_") && physicalName.substring(5).equals(expected.name())) {
            return true;
        }

        return physicalName.contains("_WALL_")
            && physicalName.replace("_WALL_", "_").equals(expected.name());
    }

    private static boolean isCauldron(@Nonnull Material material) {
        return material == Material.CAULDRON
            || material == Material.WATER_CAULDRON
            || material == Material.LAVA_CAULDRON
            || material == Material.POWDER_SNOW_CAULDRON;
    }

    /** Returns whether the block is the physical representation of the persisted item. */
    public static boolean matches(@Nonnull Block block, @Nonnull SlimefunItem item) {
        return matches(block.getType(), item.getItem().getType());
    }
}
