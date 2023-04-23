package es.bukkitbettermenus;

import org.bukkit.entity.Player;

public interface OnMenuClicked {
    void on(Player player, Menu menu, int itemNumClicked);
}
