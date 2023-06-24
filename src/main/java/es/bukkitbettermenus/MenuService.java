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
    private final MenuConstructorResolver menuConstructorResolver;
    private final StaticMenuRepository staticMenuRepository;
    private final MenuBuilderService newMenuBuilderService;
    private final OpenMenuRepository openMenuRepository;

    public MenuService() {
        this.menuConstructorResolver = new MenuConstructorResolver();
        this.staticMenuRepository = BukkitBetterMenus.STATIC_MENUS_REPOSITORY;
        this.openMenuRepository = BukkitBetterMenus.OPEN_MENUS_REPOSITORY;
        this.newMenuBuilderService = new MenuBuilderService();
    }

    public <T> Menu<T> open(Player player, Class<? extends Menu<T>> menuClass, T initialState) {
        Menu<T> menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.setState(initialState);
        menu.setPlayer(player);
        open(player, menu);

        return menu;
    }

    public Menu open(Player player, Class<? extends Menu> menuClass) {
        Menu menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.setPlayer(player);
        open(player, menu);

        return menu;
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

        menu.addPages(buildPages(player, menu));
        menu.initializeFirstPage();

        player.openInventory(menu.getInventory());

        this.openMenuRepository.save(player.getName(), menu);

        if(menu.getConfiguration().isStaticMenu()) this.staticMenuRepository.save(menu);

        callAfterShow(menu, player);
    }

    public Menu<?> buildMenu(Player player, Class<? extends Menu> menuClass) {
        Menu menu = menuConstructorResolver.getMenu(menuClass);
        menu.setPlayer(player);
        menu.addPages(buildPages(player, menu));
        openMenuRepository.save(player.getName(), menu);

        return menu;
    }

    public <T> Menu<T> buildMenu(Player player, Class<? extends Menu<T>> menuClass, T initialState) {
        Menu<T> menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.addPages(buildPages(player, menu));
        menu.setState(initialState);
        menu.setPlayer(player);
        openMenuRepository.save(player.getName(), menu);

        return menu;
    }

    public <T> List<Page> buildPages(Player player, Class<? extends Menu<T>> menuClass, T initialState) {
        Menu menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.setPlayer(player);
        menu.setState(initialState);
        openMenuRepository.save(player.getName(), menu);

        return buildPages(player, menu);
    }

    public List<Page> buildPages(Player player, Class<? extends Menu> menuClass) {
        Menu menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.setPlayer(player);
        openMenuRepository.save(player.getName(), menu);

        return buildPages(player, menu);
    }

    public List<Page> buildPages(Player player, Menu<?> menu){
        return menu.getConfiguration().isStaticMenu() ?
                this.staticMenuRepository.findByMenuClass(menu.getClass())
                        .orElse(newMenuBuilderService.createPages(menu.getConfiguration(), menu.getBaseItemNums(), player)) :
                newMenuBuilderService.createPages(menu.getConfiguration(), menu.getBaseItemNums(), player);
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
