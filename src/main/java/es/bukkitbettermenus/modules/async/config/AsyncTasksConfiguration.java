package es.bukkitbettermenus.modules.async.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@AllArgsConstructor
public final class AsyncTasksConfiguration {
    @Getter private final List<AsyncTaskOnPageLoadedConfiguration> asyncTaskOnPageLoaded;
    @Getter private final List<AsyncTaskWholeMenuConfiguration> asyncTaskWholeMenus;

    public static AsyncTasksConfiguration.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<AsyncTaskOnPageLoadedConfiguration> asyncTaskOnPageLoaded;
        private List<AsyncTaskWholeMenuConfiguration> asyncTaskWholeMenus;

        public Builder() {
            this.asyncTaskOnPageLoaded = new ArrayList<>();
            this.asyncTaskWholeMenus = new ArrayList<>();
        }

        public AsyncTasksConfiguration build() {
            return new AsyncTasksConfiguration(asyncTaskOnPageLoaded, asyncTaskWholeMenus);
        }

        public Builder onPageLoaded(int itemNum, BiConsumer<ItemStack, Integer> consumer) {
            this.asyncTaskOnPageLoaded.add(new AsyncTaskOnPageLoadedConfiguration(itemNum, consumer));
            return this;
        }

        public Builder wholeMenu(Runnable runnable) {
            this.asyncTaskWholeMenus.add(new AsyncTaskWholeMenuConfiguration(runnable));
            return this;
        }
    }
}
