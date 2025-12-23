package me.sfiguz7.extratools.implementation.interfaces;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface ETInventoryBlock {

    int[] obtenerRanurasDeEntrada();

    int[] obtenerRanurasDeSalida();

    default void crearPreset(SlimefunItem item, Consumer<BlockMenuPreset> configuracion) {
        String titulo = item.getItemName();
        new BlockMenuPreset(item.getId(), titulo) {
            public void init() {
                configuracion.accept(this);
            }

            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return flow == ItemTransportFlow.INSERT ? obtenerRanurasDeEntrada() : obtenerRanurasDeSalida();
            }

            public boolean puedeAbrir(Block b, Player p) {
                return p.hasPermission("slimefun.inventory.bypass") 
                    || Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK) 
                    && Slimefun.getPermissionsService().hasPermission(p, item);
            }
        };
    }
}
