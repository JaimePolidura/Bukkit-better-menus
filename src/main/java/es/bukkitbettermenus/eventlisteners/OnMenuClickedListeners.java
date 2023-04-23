package es.bukkitbettermenus.eventlisteners;

import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.OnMenuClicked;
import es.bukkitbettermenus.modules.confirmation.OnConfirmationMenuClicked;
import es.bukkitbettermenus.modules.numberselector.OnNumberSelectorMenuClick;
import es.bukkitbettermenus.modules.pagination.OnPaginationMenuClicked;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public final class OnMenuClickedListeners {
    private static final Set<OnMenuClicked> listeners;

    static {
        listeners = new HashSet<>();

        OnMenuClickedListeners.listen(new OnConfirmationMenuClicked());
        OnMenuClickedListeners.listen(new OnNumberSelectorMenuClick());
        OnMenuClickedListeners.listen(new OnPaginationMenuClicked());
    }


    public static void listen(OnMenuClicked onMenuClicked){
        listeners.add(onMenuClicked);
    }

    public static void notify(Player player, Menu menu, int itemNumClicked){
        listeners.forEach(listener -> {
            listener.on(player, menu, itemNumClicked);
        });
    }
}
