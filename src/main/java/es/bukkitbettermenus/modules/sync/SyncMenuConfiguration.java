package es.bukkitbettermenus.modules.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

@AllArgsConstructor
public final class SyncMenuConfiguration {
    @Getter private final BiFunction<ItemStack, Integer, ItemStack> mapper;
    @Getter private final boolean lockOnSync;

    public static SyncMenuConfigurationBuilder builder(){
        return new SyncMenuConfigurationBuilder();
    }

    public static class SyncMenuConfigurationBuilder {
        private BiFunction<ItemStack, Integer, ItemStack> mapper;
        private boolean lockOnSync;

        public SyncMenuConfigurationBuilder(){
            this.mapper = (itemStack, integer) -> itemStack;
            this.lockOnSync = false;
        }

        public SyncMenuConfigurationBuilder mapper(BiFunction<ItemStack, Integer, ItemStack> mapper){
            this.mapper = mapper;
            return this;
        }

        public SyncMenuConfigurationBuilder lockOnSync(boolean value) {
            this.lockOnSync = value;
            return this;
        }

        public SyncMenuConfiguration build(){
            return new SyncMenuConfiguration(this.mapper, this.lockOnSync);
        }
    }
}
