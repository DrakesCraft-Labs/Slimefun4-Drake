package com.github.drakescraft_labs.slimefun4.legacy.api.inventory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import dev.drake.dough.config.Config;
import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;

// This class will be deprecated, relocated and rewritten in a future version.
public class BlockMenu extends DirtyChestMenu {

    private Location location;

    private static String serializeLocation(Location l) {
        return l.getWorld().getName() + ';' + l.getBlockX() + ';' + l.getBlockY() + ';' + l.getBlockZ();
    }

    public BlockMenu(BlockMenuPreset preset, Location l) {
        super(preset);
        this.location = l;

        preset.clone(this);
        this.getContents();
    }

    public BlockMenu(BlockMenuPreset preset, Location l, Config cfg) {
        super(preset);
        this.location = l;

        for (int i = 0; i < 54; i++) {
            if (cfg.contains(String.valueOf(i))) {
                addItem(i, cfg.getItem(String.valueOf(i)));
            }
        }

        preset.clone(this);

        if (preset.getSize() > -1 && !preset.getPresetSlots().contains(preset.getSize() - 1) && cfg.contains(String.valueOf(preset.getSize() - 1))) {
            addItem(preset.getSize() - 1, cfg.getItem(String.valueOf(preset.getSize() - 1)));
        }

        this.getContents();
    }

    public void save(Location l) {
        MenuSnapshot snapshot = captureSnapshot(l);

        if (snapshot == null) {
            return;
        }

        snapshot.save();
    }

    /**
     * Bukkit inventories are main-thread state. Async autosaves must only write an
     * immutable copy, otherwise machines can persist a partial inventory during a restart.
     */
    private MenuSnapshot captureSnapshot(Location l) {
        if (Bukkit.isPrimaryThread() || Slimefun.instance() == null) {
            return captureSnapshotOnPrimaryThread(l);
        }

        try {
            return Bukkit.getScheduler().callSyncMethod(Slimefun.instance(), () -> captureSnapshotOnPrimaryThread(l)).get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while snapshotting Slimefun inventory", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("Could not snapshot Slimefun inventory", ex.getCause());
        }
    }

    private MenuSnapshot captureSnapshotOnPrimaryThread(Location l) {
        if (!isDirty()) {
            return null;
        }

        // Force the legacy menu to materialize before taking independent ItemStack copies.
        ItemStack[] liveContents = this.getContents();
        ItemStack[] contents = new ItemStack[liveContents.length];

        for (int slot : preset.getInventorySlots()) {
            ItemStack item = super.getItemInSlot(slot);
            contents[slot] = item == null ? null : item.clone();
        }

        changes = 0;
        return new MenuSnapshot(l, preset.getID(), new HashSet<>(preset.getInventorySlots()), contents);
    }

    private static final class MenuSnapshot {

        private final Location location;
        private final String presetId;
        private final Set<Integer> slots;
        private final ItemStack[] contents;

        private MenuSnapshot(Location location, String presetId, Set<Integer> slots, ItemStack[] contents) {
            this.location = location;
            this.presetId = presetId;
            this.slots = slots;
            this.contents = contents;
        }

        private void save() {
            File file = new File("data-storage/Slimefun/stored-inventories/" + serializeLocation(location) + ".sfi");
            Config cfg = new Config(file);
            cfg.setValue("preset", presetId);

            for (int slot : slots) {
                cfg.setValue(String.valueOf(slot), contents[slot]);
            }

            cfg.save();
        }
    }

    public void move(Location l) {
        this.delete(this.location);
        this.location = l;
        this.preset.newInstance(this, l);
        this.save(l);
    }

    /**
     * Reload this {@link BlockMenu} based on its {@link BlockMenuPreset}.
     */
    public void reload() {
        this.preset.clone(this);
    }

    public Block getBlock() {
        return location.getBlock();
    }

    public Location getLocation() {
        return location;
    }

    /**
     * This method drops the contents of this {@link BlockMenu} on the ground at the given
     * {@link Location}.
     * 
     * @param l
     *            Where to drop these items
     * @param slots
     *            The slots of items that should be dropped
     */
    public void dropItems(Location l, int... slots) {
        for (int slot : slots) {
            ItemStack item = getItemInSlot(slot);

            if (item != null) {
                l.getWorld().dropItemNaturally(l, item);
                replaceExistingItem(slot, null);
            }
        }
    }

    public void delete(Location l) {
        File file = new File("data-storage/Slimefun/stored-inventories/" + serializeLocation(l) + ".sfi");

        if (file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                Slimefun.logger().log(Level.WARNING, e, () -> "Could not delete file \"" + file.getName() + '"');
            }
        }
    }
}
