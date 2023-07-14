package es.bukkitbettermenus.modules.confirmation;

import es.bukkitbettermenus.configuration.ItemClickedListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

@AllArgsConstructor
public final class ConfirmationConfiguration {
    @Getter private final ConfirmationControlItem accept;
    @Getter private final ConfirmationControlItem cancel;
    @Getter private final boolean closeOnAction;

    public static ConfirmationConfigurationBuilder builder(){
        return new ConfirmationConfigurationBuilder();
    }

    public static class ConfirmationConfigurationBuilder {
        private ConfirmationControlItem accept;
        private ConfirmationControlItem cancel;
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

        public ConfirmationConfigurationBuilder accept(int itemNum, ItemStack item, ItemClickedListener onClick){
            this.accept = new ConfirmationControlItem(itemNum, item, ConfirmationControlAction.ACCEPT, onClick);
            return this;
        }

        public ConfirmationConfigurationBuilder accept(int itemNum, Material material, ItemClickedListener onClick){
            this.accept = new ConfirmationControlItem(itemNum, new ItemStack(material), ConfirmationControlAction.ACCEPT, onClick);
            return this;
        }

        public ConfirmationConfigurationBuilder cancel(int itemNum, ItemStack item, ItemClickedListener onClick){
            this.cancel = new ConfirmationControlItem(itemNum, item, ConfirmationControlAction.CANCEL, onClick);
            return this;
        }

        public ConfirmationConfigurationBuilder cancel(int itemNum, Material material, ItemClickedListener onClick){
            this.cancel = new ConfirmationControlItem(itemNum, new ItemStack(material), ConfirmationControlAction.CANCEL, onClick);
            return this;
        }
    }

    @AllArgsConstructor
    public static class ConfirmationControlItem {
        @Getter private final int itemNum;
        @Getter private ItemStack itemStack;
        @Getter private ConfirmationControlAction controlAction;
        @Getter private ItemClickedListener onClick;
    }

    private enum ConfirmationControlAction{
        ACCEPT, CANCEL;
    }
}
