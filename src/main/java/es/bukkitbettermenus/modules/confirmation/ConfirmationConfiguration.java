package es.bukkitbettermenus.modules.confirmation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

@AllArgsConstructor
public final class ConfirmationConfiguration {
    @Getter private final ConfirmationControllItem accept;
    @Getter private final ConfirmationControllItem cancel;
    @Getter private final boolean closeOnAction;

    public static ConfirmationConfigurationBuilder builder(){
        return new ConfirmationConfigurationBuilder();
    }

    public static class ConfirmationConfigurationBuilder {
        private ConfirmationControllItem accept;
        private ConfirmationControllItem cancel;
        private boolean closeOnAction;

        public ConfirmationConfigurationBuilder(){
            this.closeOnAction = true;
        }

        public ConfirmationConfiguration build(){
            return new ConfirmationConfiguration(accept, cancel, closeOnAction);
        }

        public ConfirmationConfigurationBuilder closeOnAction(boolean value){
            this.closeOnAction = value;
            return this;
        }

        public ConfirmationConfigurationBuilder accept(int itemNum, ItemStack item, BiConsumer<Player, InventoryClickEvent> onClick){
            this.accept = new ConfirmationControllItem(itemNum, item, ConfirmationControlAction.ACCEPT, onClick);
            return this;
        }

        public ConfirmationConfigurationBuilder accept(int itemNum, Material material, BiConsumer<Player, InventoryClickEvent> onClick){
            this.accept = new ConfirmationControllItem(itemNum, new ItemStack(material), ConfirmationControlAction.ACCEPT, onClick);
            return this;
        }

        public ConfirmationConfigurationBuilder cancel(int itemNum, ItemStack item, BiConsumer<Player, InventoryClickEvent> onClick){
            this.cancel = new ConfirmationControllItem(itemNum, item, ConfirmationControlAction.CANCEL, onClick);
            return this;
        }

        public ConfirmationConfigurationBuilder cancel(int itemNum, Material material, BiConsumer<Player, InventoryClickEvent> onClick){
            this.cancel = new ConfirmationControllItem(itemNum, new ItemStack(material), ConfirmationControlAction.CANCEL, onClick);
            return this;
        }
    }

    @AllArgsConstructor
    public static class ConfirmationControllItem{
        @Getter private final int itemNum;
        @Getter private ItemStack itemStack;
        @Getter private ConfirmationControlAction controlAction;
        @Getter private BiConsumer<Player, InventoryClickEvent> onClick;
    }

    private enum ConfirmationControlAction{
        ACCEPT, CANCEL;
    }
}
