package es.bukkitbettermenus;

import es.bukkitbettermenus.menubuilder.MenuBuilderService;
import es.bukkitbettermenus.menustate.AfterShow;
import es.bukkitbettermenus.menustate.BeforeShow;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import es.bukkitbettermenus.repository.StaticMenuRepository;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.ChatColor.DARK_RED;

public class MenuService {
    private final StaticMenuRepository staticMenuRepository;
    private final MenuBuilderService newMenuBuilderService;
    private final OpenMenuRepository openMenuRepository;

    public MenuService() {
        this.staticMenuRepository = BetterMenusInstanceProvider.STATIC_MENUS_REPOSITORY;
        this.openMenuRepository = BetterMenusInstanceProvider.OPEN_MENUS_REPOSITORY;
        this.newMenuBuilderService = new MenuBuilderService();
    }

    public void open(Player player, Menu menu){
        try {
            tryToOpenMenu(player, menu);
        }catch (Exception e) {
            player.sendMessage(DARK_RED + "Some error happened " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void tryToOpenMenu(Player player, Menu menu) {
        callBeforeShow(menu);

        menu.addPages(buildPages(menu));

        player.openInventory(menu.getInventory());

        this.openMenuRepository.save(player.getName(), menu);

        if(menu.getConfiguration().isStaticMenu()) this.staticMenuRepository.save(menu);

        callAfterShow(menu);
    }

    public List<Page> buildPages(Menu menu){
        return menu.getConfiguration().isStaticMenu() ?
                this.staticMenuRepository.findByMenuClass(menu.getClass())
                        .orElse(buildMenuPages(menu)) :
                buildMenuPages(menu);
    }

    private List<Page> buildMenuPages(Menu menu) {
        return newMenuBuilderService.createPages(menu.getConfiguration(), menu.getBaseItemNums());
    }

    private void callAfterShow(Menu menu) {
        if(menu instanceof AfterShow) ((AfterShow) menu).afterShow();
    }

    private void callBeforeShow(Menu menu) {
        if(menu instanceof BeforeShow) ((BeforeShow) menu).beforeShow();
    }

    public void close(Player player){
        this.openMenuRepository.findByPlayerName(player.getName()).ifPresent(menu -> {
            player.closeInventory();
        });
    }
}
