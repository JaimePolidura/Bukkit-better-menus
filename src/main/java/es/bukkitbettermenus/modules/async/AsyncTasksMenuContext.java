package es.bukkitbettermenus.modules.async;

import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.modules.async.config.AsyncTasksConfiguration;

import java.util.ArrayList;
import java.util.List;

public final class AsyncTasksMenuContext {
    private final List<AsyncTaskOnPageLoaded> asyncTasksOnPageLoaded;
    private final List<Thread> wholePageAsyncTasks;

    public AsyncTasksMenuContext(AsyncTasksConfiguration asyncConfiguration) {
        this.asyncTasksOnPageLoaded = new ArrayList<>();
        this.wholePageAsyncTasks = new ArrayList<>();

        if(asyncConfiguration != null){
            initializeThreads(asyncConfiguration);
        }
    }

    private void initializeThreads(AsyncTasksConfiguration configuration) {
        configuration.getAsyncTaskWholeMenus().forEach(asyncTaskWholeMenu -> {
            wholePageAsyncTasks.add(new Thread(asyncTaskWholeMenu.getRunnable()));
        });
        configuration.getAsyncTaskOnPageLoaded().forEach(asyncTaskOnPageLoadedConfig -> {
            asyncTasksOnPageLoaded.add(new AsyncTaskOnPageLoaded(asyncTaskOnPageLoadedConfig));
        });
    }

    public void start(Page page) {
        asyncTasksOnPageLoaded.forEach(task -> task.start(page));
        wholePageAsyncTasks.forEach(Thread::start);
    }

    public void onMenuClosed() {
        asyncTasksOnPageLoaded.forEach(AsyncTaskOnPageLoaded::onMenuClosed);
        wholePageAsyncTasks.forEach(Thread::stop);
    }

    public void onPageChanged(Page page) {
        if(!page.isAlreadyVisited()){
            asyncTasksOnPageLoaded.forEach(task -> task.onNewPage(page));
        }
    }
}
