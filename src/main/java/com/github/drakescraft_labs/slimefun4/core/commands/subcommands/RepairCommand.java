package com.github.drakescraft_labs.slimefun4.core.commands.subcommands;

import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;
import com.github.drakescraft_labs.slimefun4.core.commands.SlimefunCommand;
import com.github.drakescraft_labs.slimefun4.core.commands.SubCommand;
import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;
import com.github.drakescraft_labs.slimefun4.legacy.api.BlockStorage;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.BlockMenu;

/** Repairs the active runtime state of persisted Slimefun blocks in the sender's current chunk. */
class RepairCommand extends SubCommand {

    RepairCommand(@Nonnull Slimefun plugin, @Nonnull SlimefunCommand cmd) {
        super(plugin, cmd, "repair", true);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando debe ejecutarse dentro del juego.");
            return;
        }
        if (!player.hasPermission("slimefun.command.repair") && !player.isOp()) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        if (args.length != 2 || !args[1].equalsIgnoreCase("chunk")) {
            sender.sendMessage("Uso: /sf repair chunk");
            return;
        }

        RepairSummary summary = repairChunk(player.getLocation().getChunk());
        sender.sendMessage("§aReparación SF del chunk: " + summary.validEntries + " válido(s), "
                + summary.tickersEnabled + " ticker(s) reactivado(s), " + summary.inventoriesMarked
                + " inventario(s) marcado(s) para guardado.");
        if (summary.invalidEntries > 0 || summary.staleBlocks > 0) {
            sender.sendMessage("§eDiagnóstico sin cambios: " + summary.invalidEntries + " ID(s) desconocido(s), "
                    + summary.staleBlocks + " registro(s) con bloque ausente.");
        }
    }

    /** Re-enables safe runtime services only; unknown or stale records remain untouched for recovery. */
    private RepairSummary repairChunk(@Nonnull Chunk chunk) {
        Map<Location, me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config> storage = BlockStorage.getRawStorage(chunk.getWorld());
        if (storage == null) {
            return new RepairSummary();
        }

        RepairSummary summary = new RepairSummary();
        for (Location location : storage.keySet()) {
            if ((location.getBlockX() >> 4) != chunk.getX() || (location.getBlockZ() >> 4) != chunk.getZ()) {
                continue;
            }
            if (location.getBlock().getType().isAir()) {
                summary.staleBlocks++;
                continue;
            }

            SlimefunItem item = BlockStorage.check(location);
            if (item == null) {
                summary.invalidEntries++;
                continue;
            }

            summary.validEntries++;
            if (item.isTicking() && !item.isDisabledIn(chunk.getWorld())) {
                Slimefun.getTickerTask().enableTicker(location);
                summary.tickersEnabled++;
            }

            BlockMenu menu = BlockStorage.getInventory(location);
            if (menu != null) {
                menu.markDirty();
                summary.inventoriesMarked++;
            }
        }
        return summary;
    }

    private static final class RepairSummary {
        private int validEntries;
        private int tickersEnabled;
        private int inventoriesMarked;
        private int invalidEntries;
        private int staleBlocks;
    }
}
