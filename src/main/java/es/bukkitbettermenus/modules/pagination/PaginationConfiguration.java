package es.bukkitbettermenus.modules.pagination;

import es.bukkitbettermenus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

@AllArgsConstructor
public final class PaginationConfiguration {
    @Getter private final PaginationControlItem forward;
    @Getter private final PaginationControlItem backward;

    public static PaginationConfigurationBuilder builder(){
        return new PaginationConfigurationBuilder();
    }

    public static class PaginationConfigurationBuilder {
        private PaginationControlItem forward;
        private PaginationControlItem backward;

        public PaginationConfiguration build(){
            return new PaginationConfiguration(forward, backward);
        }

        public PaginationConfigurationBuilder forward(int itemNum, Material material){
            this.forward = new PaginationControlItem(itemNum, ItemBuilder.of(material).title(GREEN + "->").build(),
                    PaginationControlAction.FORWARD);
            return this;
        }

        public PaginationConfigurationBuilder forward(int itemNum, ItemStack itemStack){
            this.forward = new PaginationControlItem(itemNum, itemStack, PaginationControlAction.FORWARD);
            return this;
        }

        public PaginationConfigurationBuilder backward(int itemNum, Material material){
            this.backward = new PaginationControlItem(itemNum, ItemBuilder.of(material).title(RED + "<-").build(),
                    PaginationControlAction.BACKWARD);
            return this;
        }

        public PaginationConfigurationBuilder backward(int itemNum, ItemStack itemStack){
            this.backward = new PaginationControlItem(itemNum, itemStack, PaginationControlAction.BACKWARD);
            return this;
        }
    }

    @AllArgsConstructor
    public static class PaginationControlItem {
        @Getter private final int itemNum;
        @Getter private ItemStack itemStack;
        @Getter private PaginationControlAction controlAction;
    }

    private enum PaginationControlAction{
        BACKWARD, FORWARD;
    }
}
