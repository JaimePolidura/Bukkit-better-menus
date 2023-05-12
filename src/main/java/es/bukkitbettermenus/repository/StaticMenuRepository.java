package es.bukkitbettermenus.repository;

import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class StaticMenuRepository {
    private final Map<Class<? extends Menu>, List<Page>> menus;

    public StaticMenuRepository() {
        this.menus = new ConcurrentHashMap<>();
    }

    public void save(Menu menu){
        this.menus.put(menu.getClass(), menu.getPages());
    }

    public Optional<List<Page>> findByMenuClass(Class<? extends Menu> menuClass){
        return Optional.ofNullable(this.menus.get(menuClass));
    }
}
