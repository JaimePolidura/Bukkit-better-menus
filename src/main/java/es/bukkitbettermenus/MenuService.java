package es.bukkitbettermenus;

import es.bukkitbettermenus.menubuilder.CollapseService;
import es.bukkitbettermenus.menubuilder.MenuBuilderService;
import es.bukkitbettermenus.menustate.AfterShow;
import es.bukkitbettermenus.menustate.BeforeShow;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import es.bukkitbettermenus.repository.StaticMenuRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static org.bukkit.ChatColor.DARK_RED;

public class MenuService {
    private final MenuConstructorResolver menuConstructorResolver;
    private final StaticMenuRepository staticMenuRepository;
    private final MenuBuilderService newMenuBuilderService;
    private final OpenMenuRepository openMenuRepository;
    private final CollapseService collapseService;

    public MenuService() {
        this.menuConstructorResolver = BukkitBetterMenus.MENU_CONSTRUCTOR_RESOLVER;
        this.staticMenuRepository = BukkitBetterMenus.STATIC_MENUS_REPOSITORY;
        this.openMenuRepository = BukkitBetterMenus.OPEN_MENUS_REPOSITORY;
        this.newMenuBuilderService = BukkitBetterMenus.MENU_BUILDER_SERVICE;
        this.collapseService = BukkitBetterMenus.COLLAPSE_SERVICE;
    }

    public <T> Menu<T> open(Player player, Class<? extends Menu<T>> menuClass, T initialState) {
        Menu<T> menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.setState(initialState);

        open(player, menu);

        return menu;
    }

    public Menu open(Player player, Class<? extends Menu> menuClass) {
        Menu menu = this.menuConstructorResolver.getMenu(menuClass);
        open(player, menu);

        return menu;
    }

    public <T> void open(Player player, Menu<T> menu, T state) {
        menu.setState(state);

        open(player, menu);
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

        setupMenu(player, menu);

        player.openInventory(menu.getInventory());

        openMenuRepository.save(player.getName(), menu);

        if(menu.getConfiguration().isStaticMenu()) {
            staticMenuRepository.save(menu);
        }

        callAfterShow(menu, player);

        menu.startAsyncTasks();
        menu.startTimers();
    }

    public Menu<?> buildMenu(Player player, Class<? extends Menu> menuClass) {
        Menu menu = menuConstructorResolver.getMenu(menuClass);
        setupMenu(player, menu);
        openMenuRepository.save(player.getName(), menu);

        return menu;
    }

    public <T> Menu<T> buildMenu(Player player, Class<? extends Menu<T>> menuClass, T initialState) {
        Menu<T> menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.setState(initialState);
        setupMenu(player, menu);

        openMenuRepository.save(player.getName(), menu);

        return menu;
    }

    public <T> List<Page> buildPages(Player player, Class<? extends Menu<T>> menuClass, T initialState) {
        Menu menu = this.menuConstructorResolver.getMenu(menuClass);
        menu.setState(initialState);
        setupMenu(player, menu);

        openMenuRepository.save(player.getName(), menu);

        return buildPages(player, menu);
    }

    public List<Page> buildPages(Player player, Class<? extends Menu> menuClass) {
        Menu menu = this.menuConstructorResolver.getMenu(menuClass);
        setupMenu(player, menu);

        openMenuRepository.save(player.getName(), menu);

        return buildPages(player, menu);
    }

    public List<Page> buildPages(Player player, Menu<?> menu){
        return menu.getConfiguration().isStaticMenu() ?
                staticMenuRepository.findByMenuClass(menu.getClass())
                        .orElse(newMenuBuilderService.createPages(menu.getConfiguration(), menu.getBaseItemNums())) :
                newMenuBuilderService.createPages(menu.getConfiguration(), menu.getBaseItemNums());
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

    private Inventory createEmptyInventory(Menu menu) {
        SupportedInventoryType supportedInventoryType = SupportedInventoryType.getByArray(menu.items());

        return supportedInventoryType.getSize() % 9 == 0 ?
                Bukkit.createInventory(null, supportedInventoryType.getSize(), menu.getConfiguration().getTitle()) :
                Bukkit.createInventory(null, supportedInventoryType.getBukkitInventoryType(), menu.getConfiguration().getTitle());
    }

    private void setupMenu(Player player, Menu menu) {
        menu.setPlayer(player);
        menu.addPages(buildPages(player, menu));
        menu.setInventory(createEmptyInventory(menu));
        menu.initializeInventoryWithFirstPage();
        collapseService.collapse(menu);
    }
}
