package es.bukkitbettermenus.eventlisteners;

import es.bukkitbettermenus.BetterMenusInstanceProvider;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.MenuService;
import es.bukkitbettermenus.SupportedInventoryType;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.BiConsumer;

import static org.bukkit.ChatColor.DARK_RED;

public class OnInventoryClick implements Listener {
    private final OpenMenuRepository openMenuRepository;
    private final MenuService menuService;

    public OnInventoryClick() {
        this.openMenuRepository = BetterMenusInstanceProvider.OPEN_MENUS_REPOSITORY;
        this.menuService = BetterMenusInstanceProvider.MENU_SERVICE;
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        String playerName = event.getWhoClicked().getName();

        if(event.getClickedInventory() == null) return;

        this.openMenuRepository.findByPlayerName(playerName).ifPresent(menu -> {
            try{
                BetterMenusInstanceProvider.THREAD_POOL.execute(() -> {
                    tryPerformClickOnMenu(event, menu);
                });

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
            int row = SupportedInventoryType.getRowBySlot(event.getSlot(), inventoryType);
            int column = SupportedInventoryType.getColumnBySlot(event.getSlot(), inventoryType);
            int itemNumClicked = menu.getActualPage().getItemNumBySlot(event.getSlot());

            performOnClickInMenu(event, menu, row, column, itemNumClicked);
        }
    }

    private void performOnClickInMenu(InventoryClickEvent event, Menu menu, int row, int column, int itemNumClicked) {
        BiConsumer<Player, InventoryClickEvent> onClick = menu.getConfiguration().getOnClickEventListeners()
                .get(itemNumClicked);

        if (onClick != null){
            tryToExecuteOnClick(event, onClick);
        }

        OnMenuModulesClickedListeners.notify((Player) event.getWhoClicked(), menu, itemNumClicked);
    }

    private void tryToExecuteOnClick(InventoryClickEvent event, BiConsumer<Player, InventoryClickEvent> eventConsumer) {
        try{
            eventConsumer.accept((Player) event.getWhoClicked(), event);
        }catch (Exception e) {
            event.getWhoClicked().sendMessage(DARK_RED + e.getMessage());
            this.menuService.close((Player) event.getWhoClicked());
        }
    }
}
