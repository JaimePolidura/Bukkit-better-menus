package es.bukkitbettermenus.eventlisteners;

import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.menustate.AfterClose;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class OnInventoryClose implements Listener {
    private final OpenMenuRepository openMenuRepository;

    public OnInventoryClose() {
        this.openMenuRepository = BukkitBetterMenus.OPEN_MENUS_REPOSITORY;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        this.openMenuRepository.findByPlayerName(event.getPlayer().getName()).ifPresent(menu -> {
            BukkitBetterMenus.THREAD_POOL.execute(() -> {
                this.executeOnCloseEventListener(event, menu);

                this.openMenuRepository.deleteByPlayerName(event.getPlayer().getName(), menu.getClass());

                if(menu instanceof AfterClose) ((AfterClose) menu).afterClose((Player) event.getPlayer());
            });
        });
    }

    private void executeOnCloseEventListener(InventoryCloseEvent event, Menu menu){
        Consumer<InventoryCloseEvent> onCloseEventListener = menu.getConfiguration().getOnCloseEventListener();

        if(onCloseEventListener != null)
            onCloseEventListener.accept(event);
    }
}
