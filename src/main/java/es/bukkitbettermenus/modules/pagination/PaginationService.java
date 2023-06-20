package es.bukkitbettermenus.modules.pagination;

import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import org.bukkit.entity.Player;

public final class PaginationService {
    private final OpenMenuRepository openMenuRepository;

    public PaginationService() {
        this.openMenuRepository = BukkitBetterMenus.OPEN_MENUS_REPOSITORY;
    }

    public void goForward(Player player, Menu menu) {
        Page page = menu.forward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }

    public void goBackward(Player player, Menu menu) {
        Page page = menu.backward();
        player.openInventory(page.getInventory());
        this.openMenuRepository.save(player.getName(), menu);
    }

}
