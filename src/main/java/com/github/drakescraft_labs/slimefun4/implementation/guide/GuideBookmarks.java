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

import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;

/** Stores per-player guide bookmarks by Slimefun item id. */
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

    private void save() {
        try {
            data.save(file);
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "No se pudieron guardar los favoritos de la guia de Slimefun", exception);
        }
    }
}
