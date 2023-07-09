package es.bukkitbettermenus.modules.timers;

import es.bukkitbettermenus.BukkitBetterMenus;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public final class MenuTimer {
    private final BiConsumer<BukkitRunnable, Integer> onTick;
    private final TimerExecutionType executionType;
    private final long runEveryTick;

    private int timesCalled;

    private BukkitRunnable runnable;

    public void start() {
        this.runnable = new MenuTimerRunnable();

        Bukkit.getScheduler().runTaskTimer(
                BukkitBetterMenus.PLUGIN,
                runnable,
                0L,
                runEveryTick
        );
    }

    public void stop() {
        runnable.cancel();
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
