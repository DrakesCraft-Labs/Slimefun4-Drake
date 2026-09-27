package io.github.thebusybiscuit.slimefun4.utils.compatibility;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemFlag;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

public class VersionedItemFlag {
    
    public static final ItemFlag HIDE_ADDITIONAL_TOOLTIP;

    static {
        ItemFlag modern = getKey("HIDE_ADDITIONAL_TOOLTIP");
        if (modern != null) {
            HIDE_ADDITIONAL_TOOLTIP = modern;
        } else {
            ItemFlag legacy = getKey("HIDE_POTION_EFFECTS");
            HIDE_ADDITIONAL_TOOLTIP = legacy != null ? legacy : ItemFlag.HIDE_ENCHANTS;
        }
    }

    @Nullable
    private static ItemFlag getKey(@Nonnull String key) {
        try {
            Field field = ItemFlag.class.getDeclaredField(key);
            return (ItemFlag) field.get(null);
        } catch(Exception e) {
            return null;
        }
    }
}
