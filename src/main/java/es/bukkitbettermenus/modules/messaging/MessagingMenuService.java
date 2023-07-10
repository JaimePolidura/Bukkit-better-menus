package es.bukkitbettermenus.modules.messaging;

import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.repository.OpenMenuRepository;

import java.util.function.Consumer;

public final class MessagingMenuService {
    private final OpenMenuRepository openMenuRepository;

    public MessagingMenuService() {
        this.openMenuRepository = BukkitBetterMenus.OPEN_MENUS_REPOSITORY;
    }

    public <T> void broadCastMessage(Menu originalMenu, T message){
        this.openMenuRepository.findByMenuType(originalMenu.getClass()).stream()
                .filter(menu -> !menu.getMenuId().equals(originalMenu.getMenuId()))
                .forEach(menuOfPlayer -> sendMessageToMenu(message, menuOfPlayer));
    }

    public <T> void broadCastMessage(Class<? extends Menu> menuTypeTarget, T message){
        this.openMenuRepository.findByMenuType(menuTypeTarget)
                .forEach(menuOfPlayer -> sendMessageToMenu(message, menuOfPlayer));
    }

    private <T> void sendMessageToMenu(T message, Menu menuOfPlayer) {
        if(!menuOfPlayer.getConfiguration().hasMessagingConfiguration())
            throw new IllegalArgumentException("No messaging configuration added for menus");

        Consumer<T> onMessageListener = (Consumer<T>) menuOfPlayer.getConfiguration().getMessageListener(message.getClass());
        if(onMessageListener == null)
            throw new IllegalArgumentException("No message listener added for menu");

        onMessageListener.accept(message);
    }
}
