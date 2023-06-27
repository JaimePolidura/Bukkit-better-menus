package es.bukkitbettermenus.modules.sync;

import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.SupportedInventoryType;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiFunction;

public final class SyncMenuService {
    private final OpenMenuRepository openMenuRepository;

    public SyncMenuService() {
        this.openMenuRepository = BukkitBetterMenus.OPEN_MENUS_REPOSITORY;
    }

    public void sync(Class<? extends Menu> menuType, List<Page> newPages){
        this.openMenuRepository.findByMenuType(menuType).stream()
                .filter(menu -> menu.getConfiguration().isSync())
                .parallel()
                .forEach(menuToSync -> menuToSync.setPages(mapPages(
                        menuToSync,
                        menuToSync.getPages(),
                        newPages,
                        menuToSync.getConfiguration().getSyncMenuConfiguration()
                )));
    }

    public void sync(Menu originalMenu){
        this.openMenuRepository.findByMenuType(originalMenu.getClass()).stream()
                .filter(menu -> !menu.getMenuId().equals(originalMenu.getMenuId()) && menu.getConfiguration().isSync())
                .parallel()
                .forEach(menu -> menu.setPages(mapPages(
                        menu,
                        menu.getPages(),
                        originalMenu.getPages(),
                        menu.configuration().getSyncMenuConfiguration()
                )));
    }

    private List<Page> mapPages(Menu menuToMap, List<Page> oldPages, List<Page> newPages, SyncMenuConfiguration syncConfig) {
        if(syncConfig.getMapper() == null) return newPages;

        BiFunction<ItemStack, Integer, ItemStack> itemMapper = syncConfig.getMapper();

        for(int i = 0; i < newPages.size(); i++){
            Page newPage = newPages.get(i);
            Page oldPage = oldPages.get(i);

            mapPage(menuToMap, newPage, oldPage, itemMapper);
        }

        return oldPages;
    }

    private void mapPage(Menu menuToMap, Page newPage, Page oldPage, BiFunction<ItemStack, Integer, ItemStack> mapper) {
        ItemStack[] itemsNewPage = newPage.getItems().toArray(new ItemStack[0]);

        for (int j = 0; j < itemsNewPage.length; j++) {
            int row = SupportedInventoryType.getRowBySlot(j, newPage.getItemsNums());
            int column = SupportedInventoryType.getColumnBySlot(j, newPage.getItemsNums());
            int itemNum = newPage.getItemsNums()[row][column];
            ItemStack itemNewPage = itemsNewPage[j];

            ItemStack itemMapped = itemNewPage != null ?
                    mapper.apply(itemNewPage.clone(), itemNum) :
                    new ItemStack(Material.AIR);

            oldPage.setItem(itemMapped, j, itemNum);

            if(menuToMap.getActualPageId() == oldPage.getPageId()){
                menuToMap.setActualItem(j, itemMapped, itemNum);
            }
        }
    }
}
