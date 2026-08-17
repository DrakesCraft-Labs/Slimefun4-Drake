package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators;

import cl.jackstar.slimefun4.implementation.items.electric.machines.accelerators.GrowthAcceleratorTickGate;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract base class for growth accelerators.
 */
public abstract class AbstractGrowthAccelerator extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    /** Uno de cada cuatro ticks. Con menos no se nota la mejora; con mas, el cultivo se arrastra. */
    private static final int TICK_INTERVAL = 4;

    /**
     * The border slots for the menu layout.
     */
    private static final int[] BORDER = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

    /**
     * Constructs a new AbstractGrowthAccelerator.
     *
     * @param itemGroup   The item group this item belongs to
     * @param item        The item stack for this growth accelerator
     * @param recipeType  The recipe type used to craft this item
     * @param recipe      The recipe to craft this item
     */
    @ParametersAreNonnullByDefault
    protected AbstractGrowthAccelerator(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(onBreak());
        createPreset(this, this::constructMenu);
    }

    @Nonnull
    private BlockBreakHandler onBreak() {
        return new SimpleBlockBreakHandler() {

            @Override
            public void onBlockBreak(Block b) {
                BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                }
            }
        };
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int i : BORDER) {
            preset.addItem(
                    i,
                    new CustomItemStack(Material.CYAN_STAINED_GLASS_PANE, " "),
                    ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int[] getInputSlots() {
        return new int[] {10, 11, 12, 13, 14, 15, 16};
    }

    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

    @Override
    public void preRegister() {
        super.preRegister();
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, SlimefunBlockData data) {
                /*
                 * Una granja grande tiene decenas de aceleradores, y todos escaneaban su area en
                 * el mismo tick: un pico de trabajo periodico que se notaba en el TPS. La
                 * compuerta da a cada maquina una fase distinta a partir de sus coordenadas, asi
                 * que el mismo trabajo queda repartido entre varios ticks.
                 *
                 * La fase es deterministica: una maquina siempre cae en el mismo hueco, asi que
                 * el reparto no cambia entre reinicios ni depende del orden de carga.
                 */
                if (GrowthAcceleratorTickGate.shouldTick(
                        b.getWorld().getFullTime(), b.getX(), b.getY(), b.getZ(), TICK_INTERVAL)) {
                    AbstractGrowthAccelerator.this.tick(b);
                }
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
    }

    protected abstract void tick(Block b);
}
