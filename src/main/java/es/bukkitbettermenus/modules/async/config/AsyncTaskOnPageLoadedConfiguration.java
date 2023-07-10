package es.bukkitbettermenus.modules.async.config;

import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.utils.TriConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class AsyncTaskOnPageLoadedConfiguration {
    @Getter private final int itemNum;
    @Getter private final TriConsumer<Page, Integer, ItemStack> consumer;
}
