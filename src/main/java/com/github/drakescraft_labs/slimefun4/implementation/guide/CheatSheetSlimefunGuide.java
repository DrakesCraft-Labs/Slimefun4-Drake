package com.github.drakescraft_labs.slimefun4.implementation.guide;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.drakescraft_labs.slimefun4.api.items.ItemGroup;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;
import com.github.drakescraft_labs.slimefun4.api.items.groups.FlexItemGroup;
import com.github.drakescraft_labs.slimefun4.api.items.groups.SubItemGroup;
import com.github.drakescraft_labs.slimefun4.api.player.PlayerProfile;
import com.github.drakescraft_labs.slimefun4.core.guide.SlimefunGuide;
import com.github.drakescraft_labs.slimefun4.core.guide.SlimefunGuideMode;
import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;
import com.github.drakescraft_labs.slimefun4.utils.ChestMenuUtils;
import com.github.drakescraft_labs.slimefun4.utils.itemstack.SlimefunGuideItem;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

/**
 * This is an admin-variant of the {@link SurvivalSlimefunGuide} which allows a {@link Player}
 * to spawn in a {@link SlimefunItem} via click rather than showing their {@link Recipe}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class CheatSheetSlimefunGuide extends SurvivalSlimefunGuide {

    private final ItemStack item;

    public CheatSheetSlimefunGuide() {
        super(false, true);

        item = new SlimefunGuideItem(this, "&cSlimefun Guide &4(Cheat Sheet)");
    }

    /**
     * Returns a {@link List} of visible {@link ItemGroup} instances that the {@link SlimefunGuide} would display.
     *
     * @param p
     *            The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile
     *            The {@link PlayerProfile} of the {@link Player}
     * 
     * @return a {@link List} of visible {@link ItemGroup} instances
     */
    @Override
    protected List<ItemGroup> getVisibleItemGroups(@Nonnull Player p, @Nonnull PlayerProfile profile) {
        List<ItemGroup> groups = new ArrayList<>();

        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
            if (group instanceof SubItemGroup) {
                // Sub-categories belong inside their parent NestedItemGroup
                continue;
            }
            if (!(group instanceof FlexItemGroup flexItemGroup) || flexItemGroup.isVisible(p, profile, getMode())) {
                groups.add(group);
            }
        }

        groups.sort(ItemGroup.GUIDE_COMPARATOR);
        return groups;
    }

    @Override
    public @Nonnull SlimefunGuideMode getMode() {
        return SlimefunGuideMode.CHEAT_MODE;
    }

    @Override
    public @Nonnull ItemStack getItem() {
        return item;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        super.createHeader(p, profile, menu);

        // Remove Settings Panel
        menu.addItem(1, ChestMenuUtils.getBackground());
        menu.addMenuClickHandler(1, ChestMenuUtils.getEmptyClickHandler());
    }
}
