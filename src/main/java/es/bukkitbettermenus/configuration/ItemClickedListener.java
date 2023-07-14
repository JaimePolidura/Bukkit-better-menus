package es.bukkitbettermenus.configuration;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ItemClickedListener extends BiConsumer<Player, InventoryClickEvent> {
}
