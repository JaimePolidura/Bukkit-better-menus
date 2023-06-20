package es.bukkitbettermenus.modules.numberselector;

import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.OnMenuClicked;
import org.bukkit.entity.Player;

public final class OnNumberSelectorMenuClick implements OnMenuClicked {
    private final NumberSelectorService numberSelectorService;

    public OnNumberSelectorMenuClick() {
        this.numberSelectorService = BukkitBetterMenus.NUMBER_SELECTOR_SERVICE;
    }

    @Override
    public void on(Player player, Menu menu, int itemNumClicked) {
        if(hasClickedNumberSelectorItem(menu, itemNumClicked)){
            this.numberSelectorService.performNumberSelectorClicked(menu, itemNumClicked);
        }
    }

    private boolean hasClickedNumberSelectorItem(Menu menu, int itemNumClicekd){
        return menu.getConfiguration().isNumberSelector() && menu.getConfiguration().getNumberSelectorMenuConfiguration()
                .getItems().get(itemNumClicekd) != null;
    }
}
