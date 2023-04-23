package es.bukkitbettermenus.repository;

import es.bukkitbettermenus.Menu;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OpenMenuRepository {
    private final Map<String, Menu> menusByPlayerName;
    private final Map<Class<? extends Menu>, List<Menu>> menusByType;

    public OpenMenuRepository() {
        this.menusByPlayerName = new ConcurrentHashMap<>();
        this.menusByType = new ConcurrentHashMap<>();
    }

    public void save(String jugador, Menu menu){
        this.menusByPlayerName.put(jugador, menu);
        this.menusByType.putIfAbsent(menu.getClass(), new LinkedList<>());
        this.menusByType.get(menu.getClass()).add(menu);
    }

    public Optional<Menu> findByPlayerName(String playerName){
        return Optional.ofNullable(this.menusByPlayerName.get(playerName));
    }

    public List<Menu> findByMenuType(Class<? extends Menu> menuType){
        List<Menu> menus = this.menusByType.get(menuType);

        return menus == null ? Collections.EMPTY_LIST : menus;
    }

    public void deleteByPlayerName(String playerName, Class<? extends Menu> menuType){
        Menu menuRemoved = this.menusByPlayerName.remove(playerName);
        this.menusByType.get(menuType).removeIf(menu -> menu.equals(menuRemoved));
    }
}
