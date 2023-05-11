package es.bukkitbettermenus;

import es.bukkitbettermenus.menubuilder.MenuBuilderService;
import es.bukkitbettermenus.menustate.AfterShow;
import es.bukkitbettermenus.menustate.BeforeShow;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import es.bukkitbettermenus.repository.StaticMenuRepository;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.bukkit.ChatColor.DARK_RED;

public class MenuService {
    private final MenuConstructorResolver menuConstructorResolver;
    private final StaticMenuRepository staticMenuRepository;
    private final MenuBuilderService newMenuBuilderService;
    private final OpenMenuRepository openMenuRepository;

    public MenuService() {
        this.menuConstructorResolver = new MenuConstructorResolver();
        this.staticMenuRepository = BetterMenusInstanceProvider.STATIC_MENUS_REPOSITORY;
        this.openMenuRepository = BetterMenusInstanceProvider.OPEN_MENUS_REPOSITORY;
        this.newMenuBuilderService = new MenuBuilderService();
    }

    public <T> void open(Player player, Class<? extends Menu<T>> menuClass, T initialState) {
        try {
            Menu<T> menu = this.menuConstructorResolver.getMenu(menuClass);
            menu.setState(initialState);
            this.open(player, menu);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void open(Player player, Class<? extends Menu<?>> menuClass) {
        try {
            Menu<?> menu = this.menuConstructorResolver.getMenu(menuClass);
            this.open(player, menu);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void open(Player player, Menu<?> menu){
        try {
            tryToOpenMenu(player, menu);
        }catch (Exception e) {
            player.sendMessage(DARK_RED + "Some error happened " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void tryToOpenMenu(Player player, Menu<?> menu) {
        callBeforeShow(menu, player);

        menu.addPages(buildPages(menu, player));

        player.openInventory(menu.getInventory());

        this.openMenuRepository.save(player.getName(), menu);

        if(menu.getConfiguration().isStaticMenu()) this.staticMenuRepository.save(menu);

        callAfterShow(menu, player);
    }

    public List<Page> buildPages(Menu<?> menu, Player player){
        return menu.getConfiguration().isStaticMenu() ?
                this.staticMenuRepository.findByMenuClass(menu.getClass())
                        .orElse(buildMenuPages(menu, player)) :
                buildMenuPages(menu, player);
    }

    private List<Page> buildMenuPages(Menu<?> menu, Player player) {
        return newMenuBuilderService.createPages(menu.getConfiguration(), menu.getBaseItemNums(), player);
    }

    private void callAfterShow(Menu<?> menu, Player player) {
        if(menu instanceof AfterShow) ((AfterShow) menu).afterShow(player);
    }

    private void callBeforeShow(Menu<?> menu, Player player) {
        if(menu instanceof BeforeShow) ((BeforeShow) menu).beforeShow(player);
    }

    public void close(Player player){
        this.openMenuRepository.findByPlayerName(player.getName()).ifPresent(menu -> {
            player.closeInventory();
        });
    }
}
