package cl.jackstar.slimefun4.core.services;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Applies the server-wide safety boundary for non-staff Slimefun cheat access.
 */
public final class CheatPolicy {

    private static final String GUARD = "sfmaster-guard";
    private static final String CHEAT_PERMISSION = "slimefun.cheat.items";
    private static final String DEFAULT_LIMITED_PERMISSION = "odysseia.sfmaster.active";
    private static final String DEFAULT_BYPASS_PERMISSION = "slimefun.cheat.items.bypass";
    private static final String MARKER_LORE = "§cGenerado por SFMaster - No comerciable";
    private static final NamespacedKey CLAIM_HISTORY_KEY = NamespacedKey.fromString("slimefun:sfmaster_claim_history");
    private static final NamespacedKey ITEM_MARKER_KEY = NamespacedKey.fromString("odysseia:sfmaster_item");
    private static final NamespacedKey ITEM_OWNER_KEY = NamespacedKey.fromString("odysseia:sfmaster_item_owner");

    private CheatPolicy() {}

    /**
     * Returns whether this player may open and use the cheat guide at all.
     * Staff bypasses remain unrestricted while SFMaster uses the limited path.
     */
    public static boolean canUseCheat(Player player) {
        return player.isOp()
                || player.hasPermission(valueOrDefault(
                        Slimefun.getCfg().getString(GUARD + ".bypass-permission"), DEFAULT_BYPASS_PERMISSION))
                || player.hasPermission(CHEAT_PERMISSION)
                || player.hasPermission(valueOrDefault(
                        Slimefun.getCfg().getString(GUARD + ".limited-permission"), DEFAULT_LIMITED_PERMISSION));
    }

    public static boolean canClaim(Player player, SlimefunItem item) {
        if (!isLimitedPlayer(player) || item == null) {
            return true;
        }

        String id = item.getId().toUpperCase(Locale.ROOT);
        return isExplicitlyAllowed(item, id)
                && !matchesAny(id, list("blocked-id-prefixes"), true)
                && !matchesAny(id, list("blocked-id-fragments"), false)
                && !blockedAddon(item.getAddon())
                && !blockedMaterial(item.getItem().getType());
    }

    /**
     * Single delivery gate for category pages and search results.
     * Limited claims are one item, persisted across restarts and marked before insertion.
     */
    public static boolean claim(Player player, SlimefunItem item, boolean shiftClicked) {
        if (!canUseCheat(player)) {
            Slimefun.getLocalization().sendMessage(player, "messages.no-permission", true);
            return false;
        }
        if (!canClaim(player, item)) {
            deny(player);
            return false;
        }

        boolean limited = isLimitedPlayer(player);
        ItemStack claimed = item.getItem().clone();
        claimed.setAmount(limited ? 1 : (shiftClicked ? claimed.getMaxStackSize() : 1));

        if (limited) {
            markClaim(claimed, player.getUniqueId());
        }
        if (!hasSpace(player, claimed)) {
            player.sendMessage("§cNecesitas espacio libre para reclamar este objeto.");
            return false;
        }
        ClaimWindow.Result pendingClaim = limited ? evaluatePersistentClaim(player) : null;
        if (pendingClaim != null && !pendingClaim.allowed()) {
            int maximum = Math.max(1, intSetting("max-claims", 12));
            int minutes = Math.max(1, intSetting("window-minutes", 60));
            player.sendMessage("§cLímite SFMaster alcanzado: " + maximum + " reclamos cada " + minutes + " minutos.");
            return false;
        }

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(claimed);
        if (!leftovers.isEmpty()) {
            player.sendMessage("§cNo se pudo entregar el objeto; libera un espacio e inténtalo nuevamente.");
            return false;
        }
        if (limited) {
            persistClaim(player, pendingClaim);
            Slimefun.logger().info("[SFMaster] " + player.getName() + " reclamó " + item.getId() + " x1.");
        }
        return true;
    }

    public static void deny(Player player) {
        player.sendMessage("§cSFMaster solo permite materiales y automatización expresamente auditados.");
        player.sendMessage("§7Equipamiento, endgame y addons no aprobados permanecen bloqueados.");
    }

    public static boolean isLimitedPlayer(Player player) {
        Boolean enabled = Slimefun.getCfg().getBoolean(GUARD + ".enabled");
        if ((enabled != null && !enabled) || player.isOp()) {
            return false;
        }

        String bypass =
                valueOrDefault(Slimefun.getCfg().getString(GUARD + ".bypass-permission"), DEFAULT_BYPASS_PERMISSION);
        if (player.hasPermission(bypass)) {
            return false;
        }

        String limitedPermission =
                valueOrDefault(Slimefun.getCfg().getString(GUARD + ".limited-permission"), DEFAULT_LIMITED_PERMISSION);
        return player.hasPermission(CHEAT_PERMISSION) || player.hasPermission(limitedPermission);
    }

    static ClaimWindow.Result evaluateWindow(long[] history, long now, long windowMillis, int maximum) {
        return ClaimWindow.consume(history, now, windowMillis, maximum);
    }

    private static boolean isExplicitlyAllowed(SlimefunItem item, String id) {
        String addonName =
                item.getAddon() == null ? "" : item.getAddon().getName().toUpperCase(Locale.ROOT);
        return list("allowed-addons").stream().anyMatch(value -> addonName.contains(value.toUpperCase(Locale.ROOT)))
                || matchesAny(id, list("allowed-id-prefixes"), true)
                || list("allowed-item-ids").stream().anyMatch(value -> id.equalsIgnoreCase(value));
    }

    private static boolean blockedAddon(SlimefunAddon addon) {
        return addon != null && matchesAny(addon.getName().toUpperCase(Locale.ROOT), list("blocked-addons"), false);
    }

    private static boolean blockedMaterial(Material material) {
        String name = material.name().toUpperCase(Locale.ROOT);
        return list("blocked-materials").stream().anyMatch(value -> value.equalsIgnoreCase(name))
                || matchesAny(name, list("blocked-id-fragments"), false);
    }

    private static boolean matchesAny(String value, List<String> patterns, boolean prefix) {
        for (String pattern : patterns) {
            String normalized = pattern.toUpperCase(Locale.ROOT);
            if ((prefix && value.startsWith(normalized)) || (!prefix && value.contains(normalized))) {
                return true;
            }
        }
        return false;
    }

    private static List<String> list(String path) {
        return Slimefun.getCfg().getStringList(GUARD + "." + path);
    }

    private static int intSetting(String path, int fallback) {
        Integer value = Slimefun.getCfg().getInt(GUARD + "." + path);
        return value == null ? fallback : value;
    }

    private static ClaimWindow.Result evaluatePersistentClaim(Player player) {
        if (CLAIM_HISTORY_KEY == null) {
            return new ClaimWindow.Result(false, new long[0]);
        }
        PersistentDataContainer data = player.getPersistentDataContainer();
        long[] history = data.get(CLAIM_HISTORY_KEY, PersistentDataType.LONG_ARRAY);
        long now = System.currentTimeMillis();
        long windowMillis = Math.max(1, intSetting("window-minutes", 60)) * 60_000L;
        int maximum = Math.max(1, intSetting("max-claims", 12));
        return ClaimWindow.consume(history, now, windowMillis, maximum);
    }

    private static void persistClaim(Player player, ClaimWindow.Result result) {
        if (CLAIM_HISTORY_KEY == null || result == null || !result.allowed()) {
            throw new IllegalStateException("No se pudo persistir el reclamo SFMaster");
        }
        player.getPersistentDataContainer().set(CLAIM_HISTORY_KEY, PersistentDataType.LONG_ARRAY, result.history());
    }

    private static void markClaim(ItemStack item, UUID owner) {
        if (ITEM_MARKER_KEY == null || ITEM_OWNER_KEY == null) {
            throw new IllegalStateException("No se pudieron crear las claves persistentes de SFMaster");
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("El objeto SFMaster no admite metadatos persistentes");
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ITEM_MARKER_KEY, PersistentDataType.BYTE, (byte) 1);
        data.set(ITEM_OWNER_KEY, PersistentDataType.STRING, owner.toString());
        List<String> lore = meta.hasLore() ? new java.util.ArrayList<>(meta.getLore()) : new java.util.ArrayList<>();
        if (!lore.contains(MARKER_LORE)) {
            lore.add("");
            lore.add(MARKER_LORE);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private static boolean hasSpace(Player player, ItemStack item) {
        for (ItemStack current : player.getInventory().getStorageContents()) {
            if (current == null || current.getType().isAir()) {
                return true;
            }
            if (current.isSimilar(item) && current.getAmount() + item.getAmount() <= current.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
