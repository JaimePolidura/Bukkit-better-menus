package es.bukkitbettermenus.modules.confirmation;

import es.bukkitbettermenus.BetterMenusInstanceProvider;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.MenuService;
import es.bukkitbettermenus.OnMenuClicked;
import org.bukkit.entity.Player;

public final class OnConfirmationMenuClicked implements OnMenuClicked {
    private final MenuService menuService;

    public OnConfirmationMenuClicked() {
        this.menuService = BetterMenusInstanceProvider.MENU_SERVICE;
    }

    @Override
    public void on(Player player, Menu menu, int itemNumClicked) {
        if(hasClickedConfirmationItems(menu, itemNumClicked)){
            if(menu.getConfiguration().getConfirmationConfiguration().isCloseOnAction())
                this.menuService.close(player);
        }
    }

    private boolean hasClickedConfirmationItems(Menu menu, int itemNumClicked){
        return menu.getConfiguration().isConfirmation() && (menu.getConfiguration().getConfirmationConfiguration().getCancel().getItemNum() == itemNumClicked ||
                menu.getConfiguration().getConfirmationConfiguration().getAccept().getItemNum() == itemNumClicked);
    }
}
