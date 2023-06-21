package es.bukkitbettermenus;

import es.bukkitbettermenus.eventlisteners.OnInventoryClick;
import es.bukkitbettermenus.eventlisteners.OnInventoryClose;
import es.bukkitbettermenus.menubuilder.MenuBuilderService;
import es.bukkitbettermenus.modules.messaging.MessagingMenuService;
import es.bukkitbettermenus.modules.numberselector.NumberSelectorService;
import es.bukkitbettermenus.modules.pagination.PaginationService;
import es.bukkitbettermenus.modules.sync.SyncMenuService;
import es.bukkitbettermenus.repository.OpenMenuRepository;
import es.bukkitbettermenus.repository.StaticMenuRepository;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class BukkitBetterMenus {
    public static final OpenMenuRepository OPEN_MENUS_REPOSITORY = new OpenMenuRepository();
    public static final StaticMenuRepository STATIC_MENUS_REPOSITORY = new StaticMenuRepository();
    public static final MenuService MENU_SERVICE = new MenuService();
    public static final MessagingMenuService MESSAGING_MENU_SERVICE = new MessagingMenuService();
    public static final NumberSelectorService NUMBER_SELECTOR_SERVICE = new NumberSelectorService();
    public static final PaginationService PAGINATION_SERVICE = new PaginationService();
    public static final MenuBuilderService MENU_BUILDER_SERVICE = new MenuBuilderService();
    public static final SyncMenuService SYNC_MENU_SERVICE = new SyncMenuService();
    public static MenusDependenciesInstanceProvider INSTANCE_PROVIDER = null;

    public static void setInstanceProvider(MenusDependenciesInstanceProvider menusDependenciesInstanceProvider) {
        INSTANCE_PROVIDER = menusDependenciesInstanceProvider;
    }

    public static void registerEventListeners(Plugin plugin, PluginManager pluginManager) {
        pluginManager.registerEvents(new OnInventoryClick(), plugin);
        pluginManager.registerEvents(new OnInventoryClose(), plugin);
    }
}
