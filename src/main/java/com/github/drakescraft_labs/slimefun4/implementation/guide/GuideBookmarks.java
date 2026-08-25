package com.github.drakescraft_labs.slimefun4.implementation.guide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.drakescraft_labs.slimefun4.api.items.ItemGroup;
import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;

/**
 * Stores per-player guide bookmarks.
 * <p>
 * Two kinds of favorites are tracked: individual Slimefun item ids (the original,
 * on-disk-compatible format) and item group keys, which addons such as a
 * {@link com.github.drakescraft_labs.slimefun4.api.items.groups.FlexItemGroup} can use to let
 * players bookmark a whole custom menu instead of a single item. Group keys are resolved
 * dynamically against {@link com.github.drakescraft_labs.slimefun4.core.SlimefunRegistry} so a
 * group removed by a disabled/updated addon simply disappears from the resolved list instead of
 * throwing.
 */
public final class GuideBookmarks {

    private static GuideBookmarks instance;

    private final File file;
    private final Logger logger;
    private final YamlConfiguration data;

    GuideBookmarks(@Nonnull File file, @Nonnull Logger logger) {
        this.file = file;
        this.logger = logger;
        data = YamlConfiguration.loadConfiguration(file);
    }

    public static synchronized void initialize(@Nonnull Slimefun plugin) {
        instance = new GuideBookmarks(new File(plugin.getDataFolder(), "guide-bookmarks.yml"), plugin.getLogger());
    }

    public static @Nonnull GuideBookmarks get() {
        if (instance == null) {
            throw new IllegalStateException("Guide bookmarks were accessed before initialization");
        }

        return instance;
    }

    public synchronized boolean contains(@Nonnull UUID playerId, @Nonnull String itemId) {
        return read(playerId).contains(itemId);
    }

    public synchronized int size(@Nonnull UUID playerId) {
        return read(playerId).size();
    }

    public synchronized @Nonnull List<String> getBookmarks(@Nonnull UUID playerId) {
        return new ArrayList<>(read(playerId));
    }

    /**
     * Toggles one bookmark and immediately persists the new state.
     *
     * @return {@code true} when added, {@code false} when removed
     */
    public synchronized boolean toggle(@Nonnull UUID playerId, @Nonnull String itemId) {
        Set<String> ids = read(playerId);
        boolean added = ids.add(itemId);

        if (!added) {
            ids.remove(itemId);
        }

        data.set(playerId.toString(), new ArrayList<>(ids));
        save();
        return added;
    }

    private @Nonnull Set<String> read(@Nonnull UUID playerId) {
        return new LinkedHashSet<>(data.getStringList(playerId.toString()));
    }

    public synchronized boolean containsGroup(@Nonnull UUID playerId, @Nonnull String groupKey) {
        return readGroups(playerId).contains(groupKey);
    }

    public synchronized int groupSize(@Nonnull UUID playerId) {
        return readGroups(playerId).size();
    }

    public synchronized @Nonnull List<String> getGroupBookmarks(@Nonnull UUID playerId) {
        return new ArrayList<>(readGroups(playerId));
    }

    /**
     * Resolves the player's bookmarked group keys against the current
     * {@link com.github.drakescraft_labs.slimefun4.core.SlimefunRegistry}, silently dropping keys
     * that no longer match a registered {@link ItemGroup} (e.g. the owning addon was disabled).
     * Nothing is deleted from disk, so the bookmark reappears if the group is registered again.
     */
    public synchronized @Nonnull List<ItemGroup> getResolvedGroupBookmarks(@Nonnull UUID playerId) {
        List<String> keys = getGroupBookmarks(playerId);
        List<ItemGroup> resolved = new ArrayList<>(keys.size());

        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
            if (keys.contains(group.getKey().toString())) {
                resolved.add(group);
            }
        }

        return resolved;
    }

    /**
     * Toggles one group bookmark and immediately persists the new state.
     *
     * @return {@code true} when added, {@code false} when removed
     */
    public synchronized boolean toggleGroup(@Nonnull UUID playerId, @Nonnull String groupKey) {
        Set<String> ids = readGroups(playerId);
        boolean added = ids.add(groupKey);

        if (!added) {
            ids.remove(groupKey);
        }

        data.set(groupPath(playerId), new ArrayList<>(ids));
        save();
        return added;
    }

    private @Nonnull Set<String> readGroups(@Nonnull UUID playerId) {
        return new LinkedHashSet<>(data.getStringList(groupPath(playerId)));
    }

    /**
     * Group bookmarks live under a separate top-level {@code groups} branch so they never collide
     * with the flat item-id list already stored directly under {@code playerId}.
     */
    private @Nonnull String groupPath(@Nonnull UUID playerId) {
        return "groups." + playerId;
    }

    private void save() {
        try {
            data.save(file);
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "No se pudieron guardar los favoritos de la guia de Slimefun", exception);
        }
    }
}
