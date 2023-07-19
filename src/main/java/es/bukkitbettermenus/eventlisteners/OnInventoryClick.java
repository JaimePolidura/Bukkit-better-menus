package es.bukkitbettermenus.eventlisteners;

import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.MenuService;
import es.bukkitbettermenus.configuration.ItemClickedListener;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

import static org.bukkit.ChatColor.DARK_RED;

public class OnInventoryClick implements Listener {
    private final OpenMenuRepository openMenuRepository;
    private final MenuService menuService;

    public OnInventoryClick() {
        this.openMenuRepository = BukkitBetterMenus.OPEN_MENUS_REPOSITORY;
        this.menuService = BukkitBetterMenus.MENU_SERVICE;
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        String playerName = event.getWhoClicked().getName();

        if(event.getClickedInventory() == null) return;

        event.getWhoClicked().sendMessage();

        this.openMenuRepository.findByPlayerName(playerName).ifPresent(menu -> {
            try{
                tryPerformClickOnMenu(event, menu);
            }catch (Exception e) {
                event.getWhoClicked().sendMessage(DARK_RED + "Some error happened " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void tryPerformClickOnMenu(InventoryClickEvent event, Menu menu) {
        if(menu.getConfiguration().isFixedItems())
            event.setCancelled(true);

        InventoryType inventoryType = event.getClickedInventory().getType();
        boolean inventorTypePlayer = event.getCurrentItem() == null || inventoryType == InventoryType.PLAYER;

        if (!inventorTypePlayer){
            int itemNumClicked = menu.getActualItemNumBySlot(event.getSlot());

            ItemClickedListener itemClickedListener = getItemClickedListener(menu, event.getAction(), itemNumClicked);

            if(itemClickedListener != null){
                performOnClickInMenu(event, menu, itemNumClicked, itemClickedListener);
            }
        }
    }

    private void performOnClickInMenu(InventoryClickEvent event, Menu menu, int itemNumClicked, ItemClickedListener itemClickedListener) {
        try{
            itemClickedListener.accept((Player) event.getWhoClicked(), event);

            OnMenuModulesClickedListeners.notify((Player) event.getWhoClicked(), menu, itemNumClicked);
        }catch (Exception e) {
            event.getWhoClicked().sendMessage(DARK_RED + e.getMessage());
            this.menuService.close((Player) event.getWhoClicked());
        }
    }

    @Nullable
    private ItemClickedListener getItemClickedListener(Menu menu, InventoryAction action, int itemNum) {
        boolean isLeftClick = action == InventoryAction.NOTHING || action == InventoryAction.PICKUP_ALL;
        boolean isRightClick = action == InventoryAction.PICKUP_HALF;

        if(isLeftClick){
            return menu.getConfiguration().getOnLeftClickEventListeners().get(itemNum);
        }
        if(isRightClick){
            return menu.getConfiguration().getOnRightClickEventListeners().get(itemNum);
        }

        return null;
    }
}
