package com.github.drakescraft_labs.slimefun4.api.items.groups;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.drakescraft_labs.slimefun4.api.items.ItemGroup;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;
import com.github.drakescraft_labs.slimefun4.api.player.PlayerProfile;
import com.github.drakescraft_labs.slimefun4.core.guide.SlimefunGuide;
import com.github.drakescraft_labs.slimefun4.core.guide.SlimefunGuideMode;
import com.github.drakescraft_labs.slimefun4.implementation.guide.GuideBookmarks;

/**
 * A {@link FlexItemGroup} is a {@link ItemGroup} inside the {@link SlimefunGuide} that can
 * be completely modified.
 * It cannot hold any {@link SlimefunItem} but can be completely overridden
 * to perform any action upon being opened.
 * 
 * @author TheBusyBiscuit
 *
 */
public abstract class FlexItemGroup extends ItemGroup {

    @ParametersAreNonnullByDefault
    protected FlexItemGroup(NamespacedKey key, ItemStack item) {
        this(key, item, 3);
    }

    @ParametersAreNonnullByDefault
    protected FlexItemGroup(NamespacedKey key, ItemStack item, int tier) {
        super(key, item, tier);
    }

    @Override
    public final boolean isVisible(@Nonnull Player p) {
        /*
         * We can stop this method right here.
         * We provide a custom method with more parameters for this.
         * See isVisible(...)
         */
        return true;
    }

    /**
     * This method returns whether this {@link FlexItemGroup} is visible under the given context.
     * Implementing this method gives full flexibility over who can see the ItemGroup when and where.
     * 
     * @param p
     *            The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile
     *            The {@link PlayerProfile} of the {@link Player}
     * @param layout
     *            The {@link SlimefunGuideMode} in which this {@link FlexItemGroup} is viewed
     * 
     * @return Whether to display this {@link FlexItemGroup}
     */
    @ParametersAreNonnullByDefault
    public abstract boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout);

    /**
     * This method is called when a {@link Player} opens this {@link FlexItemGroup}.
     * This is an abstract method which needs to be implemented in order to determine what this
     * {@link FlexItemGroup} should actually do as it cannot hold any items.
     * 
     * @param p
     *            The {@link Player} who wants to open this {@link FlexItemGroup}
     * @param profile
     *            The corresponding {@link PlayerProfile} for that {@link Player}
     * @param layout
     *            The current {@link SlimefunGuideMode}
     */
    public abstract void open(Player p, PlayerProfile profile, SlimefunGuideMode layout);

    /**
     * Whether {@code p} has bookmarked this whole {@link FlexItemGroup} as a favorite.
     * <p>
     * This is deliberately opt-in: the core guide never renders a favorite icon or intercepts
     * clicks inside {@link #open(Player, PlayerProfile, SlimefunGuideMode)} on its own, since doing
     * so could shadow click handlers a machine-driven menu relies on. An implementation decides for
     * itself where to place the icon and whether to call {@link #toggleFavorite(Player)} from its
     * own click handler.
     *
     * @param p
     *            The {@link Player} to check
     *
     * @return whether the group is bookmarked
     */
    @ParametersAreNonnullByDefault
    protected final boolean isFavorite(Player p) {
        return GuideBookmarks.get().containsGroup(p.getUniqueId(), getKey().toString());
    }

    /**
     * Toggles whether {@code p} has bookmarked this whole {@link FlexItemGroup} and persists the
     * change immediately. See {@link #isFavorite(Player)} for why this is opt-in rather than
     * automatic.
     *
     * @param p
     *            The {@link Player} toggling their bookmark
     *
     * @return {@code true} when the group was added to favorites, {@code false} when removed
     */
    @ParametersAreNonnullByDefault
    protected final boolean toggleFavorite(Player p) {
        return GuideBookmarks.get().toggleGroup(p.getUniqueId(), getKey().toString());
    }

    @Override
    public final void add(@Nonnull SlimefunItem item) {
        throw new UnsupportedOperationException("You cannot add items to a FlexItemGroup!");
    }

    @Override
    public final @Nonnull List<SlimefunItem> getItems() {
        throw new UnsupportedOperationException("A FlexItemGroup has no items!");
    }

    @Override
    public final boolean contains(SlimefunItem item) {
        throw new UnsupportedOperationException("A FlexItemGroup has no items!");
    }

    @Override
    public final void remove(@Nonnull SlimefunItem item) {
        throw new UnsupportedOperationException("A FlexItemGroup has no items, so there is nothing remove!");
    }

}
