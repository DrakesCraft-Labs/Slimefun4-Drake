package com.github.drakescraft_labs.slimefun4.api.items;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;

/**
 * Bukkit admite {@link org.bukkit.inventory.ItemStack#setItemMeta(ItemMeta)} con {@code null}
 * para retirar la meta de un item, y CoreProtect lo invoca al registrar los drops de una
 * entidad que muere. Antes de este arreglo el override de {@link SlimefunItemStack} pasaba ese
 * null directo al constructor de ItemMetaSnapshot, que lo desreferenciaba: cada aldeano matado
 * por un jugador reventaba EntityDeathEvent con una NullPointerException y CoreProtect perdia
 * la auditoria de esa muerte. Ticket 384.
 */
class TestSlimefunItemStackNullMeta {

    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testSetItemMetaAcceptsNull() {
        SlimefunItemStack item = new SlimefunItemStack("NULL_META_TEST", Material.DIAMOND, "&aItem de prueba");

        Assertions.assertDoesNotThrow(() -> item.setItemMeta(null), "setItemMeta(null) no debe lanzar excepcion");
        Assertions.assertNotNull(item.getItemMetaSnapshot(), "la instantanea debe existir tras retirar la meta");
        Assertions.assertFalse(item.getItemMetaSnapshot().getDisplayName().isPresent(),
                "una meta retirada no deja nombre en la instantanea");
    }

    @Test
    void testSetItemMetaKeepsSnapshotInSync() {
        SlimefunItemStack item = new SlimefunItemStack("SYNC_META_TEST", Material.DIAMOND, "&aItem de prueba");

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Nombre nuevo");
        item.setItemMeta(meta);

        Assertions.assertEquals("Nombre nuevo", item.getItemMetaSnapshot().getDisplayName().orElse(null),
                "la instantanea debe seguir reflejando la meta aplicada");
    }
}
