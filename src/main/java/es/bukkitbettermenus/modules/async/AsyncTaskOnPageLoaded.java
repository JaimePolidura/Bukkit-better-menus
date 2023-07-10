package es.bukkitbettermenus.modules.async;

import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.modules.async.config.AsyncTaskOnPageLoadedConfiguration;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class AsyncTaskOnPageLoaded {
    @Getter private final AsyncTaskOnPageLoadedConfiguration configuration;
    @Getter private final List<Thread> threads;

    public AsyncTaskOnPageLoaded(AsyncTaskOnPageLoadedConfiguration configuration) {
        this.configuration = configuration;
        this.threads = new ArrayList<>();
    }

    public void onNewPage(Page page) {
        start(page);
    }

    public void start(Page page) {
        Thread thread = new Thread(() -> {
            List<ItemStack> items = page.getItemsByItemNum(configuration.getItemNum());
            int initialSlot = page.getSlotByItemNum(configuration.getItemNum());
            
            for (int i = 0; i < items.size(); i++) {
                if(Thread.currentThread().isInterrupted()){
                    break;
                }

                ItemStack item = items.get(i);
                int actualSlot = initialSlot + i;

                configuration.getConsumer().consume(page, actualSlot, item);
            }
        });

        threads.add(thread);

        thread.start();
    }

    public void onMenuClosed() {
        threads.forEach(Thread::interrupt);
    }
}
