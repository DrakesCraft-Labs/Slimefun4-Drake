package com.github.drakescraft_labs.slimefun4.utils;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;

/** Validates that persisted Slimefun metadata still matches the physical block in the world. */
public final class BlockStorageIntegrity {

    private BlockStorageIntegrity() {}

    /**
     * Checks if the item is a virtual, display or organic item that legitimately operates on
     * AIR, custom entities, or multi-state block transformations (like Cultivation flora, SefiLib Display blocks, etc.).
     */
    public static boolean isVirtualOrDisplayItem(SlimefunItem item) {
        if (item == null) {
            return false;
        }

        String id = item.getId();
        if (id.startsWith("CLT_")
                || id.startsWith("NETHEO_")
                || id.startsWith("FP_")
                || id.equals("EXPERIENCE_CAULDRON")
                || id.equals("FLOWER_POWER_MAGIC_BASIN")) {
            return true;
        }

        try {
            if (item.getAddon() != null) {
                String addonName = item.getAddon().getName();
                if ("Cultivation".equalsIgnoreCase(addonName)
                        || "Netheopoiesis".equalsIgnoreCase(addonName)
                        || "FlowerPower".equalsIgnoreCase(addonName)
                        || "SefiLib".equalsIgnoreCase(addonName)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        String className = item.getClass().getName();
        return className.contains("dev.sefiraat.cultivation")
                || className.contains("dev.sefiraat.netheopoiesis")
                || className.contains("dev.drake.sefilib")
                || className.contains("dev.sefiraat.sefilib")
                || className.contains("io.ncbpfluffybear.flowerpower");
    }

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

    public static boolean matches(@Nonnull Material physical, @Nonnull SlimefunItem item) {
        if (isVirtualOrDisplayItem(item)) {
            return true;
        }
        return matches(physical, item.getItem().getType());
    }

    private static boolean isCauldron(@Nonnull Material material) {
        return material == Material.CAULDRON
            || material == Material.WATER_CAULDRON
            || material == Material.LAVA_CAULDRON
            || material == Material.POWDER_SNOW_CAULDRON;
    }

    /** Returns whether the block is the physical representation of the persisted item. */
    public static boolean matches(@Nonnull Block block, @Nonnull SlimefunItem item) {
        if (isVirtualOrDisplayItem(item)) {
            return true;
        }
        return matches(block.getType(), item.getItem().getType());
    }
}
