package es.bukkitbettermenus.modules.async.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

@AllArgsConstructor
public class AsyncTaskOnPageLoadedConfiguration {
    @Getter private final int itemNum;
    @Getter private final BiConsumer<ItemStack, Integer> consumer;
}
