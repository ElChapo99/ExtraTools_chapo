package me.sfiguz7.extratools.implementation.machines;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.sfiguz7.extratools.implementation.interfaces.ETInventoryBlock;
import me.sfiguz7.extratools.lists.ETItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class GeneradorDePiedra extends SimpleSlimefunItem<BlockTicker> implements ETInventoryBlock,
    EnergyNetComponent {

    private static final int CONSUMO_DE_ENERGIA = 32;
    private final int[] borde = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 18, 19, 20, 21, 22, 27, 28, 29, 30,
        31, 36, 37, 38, 39, 40, 41, 42, 43, 44, 22};
    private final int[] bordeEntrada = {};
    private final int[] bordeSalida = {14, 15, 16, 17, 23, 26, 32, 33, 34, 35};
    private int decremento = 2;

    public GeneradorDePiedra() {
        super(ETItems.extra_tools, ETItems.COBBLESTONE_GENERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
            new ItemStack[] {SlimefunItems.PROGRAMMABLE_ANDROID_MINER, SlimefunItems.MAGNESIUM_INGOT,
                SlimefunItems.PROGRAMMABLE_ANDROID_MINER,
                new ItemStack(Material.WATER_BUCKET), SlimefunItems.BLISTERING_INGOT_3,
                new ItemStack(Material.LAVA_BUCKET),
                SlimefunItems.PROGRAMMABLE_ANDROID_MINER, SlimefunItems.BIG_CAPACITOR,
                SlimefunItems.PROGRAMMABLE_ANDROID_MINER});

        crearPreset(this, this::construirMenu);

        addItemHandler(alRomper());
    }

    private void construirMenu(BlockMenuPreset preset) {
        for (int i : borde) {
            preset.addItem(i, new CustomItemStack(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "),
                ChestMenuUtils.getEmptyClickHandler());
        }
        for (int i : bordeEntrada) {
            preset.addItem(i, new CustomItemStack(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), " "),
                ChestMenuUtils.getEmptyClickHandler());
        }
        for (int i : bordeSalida) {
            preset.addItem(i, new CustomItemStack(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE), " "),
                ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new ChestMenu.AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor,
                                       ClickAction action) {
                    return cursor == null || cursor.getType() == Material.AIR;
                }
            });
        }
    }

    @Override
    public int[] getInputSlots() {
        return new int[] {};
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] {24, 25};
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return 512;
    }

    public BlockBreakHandler alRomper() {
        return new BlockBreakHandler(false, false) {

            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Block b = e.getBlock();
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }

    @Override
    public BlockTicker getItemHandler() {
        return new BlockTicker() {

            @Override
            // ¡Se ejecuta primero! El método tick() se ejecuta después
            public void uniqueTick() {
                // Necesario para controlar todos los generadores de piedra a la vez,
                // Solo hace que vuelva al máximo (por ahora 2, será personalizable)
                // cuando llega al mínimo posible (es decir 1)
                if (decremento == 1) {
                    decremento = 2;
                    return;
                }
                decremento--;

            }

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                // Solo actuamos una vez por ciclo de decremento, cuando decremento llega
                // al mínimo y se ha reiniciado
                if (decremento != 2) {
                    return;
                }

                ItemStack salida = new ItemStack(Material.COBBLESTONE);

                if (getCharge(b.getLocation()) >= CONSUMO_DE_ENERGIA) {
                    BlockMenu menu = BlockStorage.getInventory(b);

                    if (!menu.fits(salida, getOutputSlots())) {
                        return;
                    }

                    removeCharge(b.getLocation(), CONSUMO_DE_ENERGIA);
                    menu.pushItem(salida, getOutputSlots());
                }
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        };
    }

}
