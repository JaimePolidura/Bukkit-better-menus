package es.bukkitbettermenus.modules.timers;

import es.bukkitbettermenus.BukkitBetterMenus;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;

public final class MenuTimer {
    private final BiConsumer<BukkitRunnable, Integer> onTick;
    private final TimerExecutionType executionType;
    private final long runEveryTick;
    private final BukkitRunnable runnable;

    private int timesCalled;

    public MenuTimer(BiConsumer<BukkitRunnable, Integer> onTick, TimerExecutionType executionType, long runEveryTick) {
        this.onTick = onTick;
        this.executionType = executionType;
        this.runEveryTick = runEveryTick;
        this.runnable = new MenuTimerRunnable();
        this.timesCalled = 0;
    }
    
    public void start() {
        this.runnable.runTaskTimer(BukkitBetterMenus.PLUGIN, 0L, runEveryTick);
    }

    public void stop() {
        Try.runRunnable(runnable::cancel);
    }

    public static MenuTimer createTimer(TimerExecutionType executionType, long runEveryTick, BiConsumer<BukkitRunnable, Integer> onTick) {
        return new MenuTimer(onTick, executionType, runEveryTick);
    }

    private final class MenuTimerRunnable extends BukkitRunnable {
        @Override
        public void run() {
            timesCalled++;

            onTick.accept(this, timesCalled);

            if(executionType == TimerExecutionType.RUN_ONCE){
                cancel();
            }
        }
    }
}
