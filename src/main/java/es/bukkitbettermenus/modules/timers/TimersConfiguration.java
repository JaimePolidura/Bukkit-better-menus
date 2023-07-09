package es.bukkitbettermenus.modules.timers;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class TimersConfiguration {
    @Getter private final List<MenuTimer> timers;

    public TimersConfiguration(){
        this.timers = new ArrayList<>();
    }

    public static TimersConfiguration build() {
        return new TimersConfiguration();
    }

    public TimersConfiguration addTimer(TimerExecutionType executionType, long runEveryTick, BiConsumer<BukkitRunnable, Integer> onTick) {
        this.timers.add(new MenuTimer(executionType, runEveryTick, onTick));
        return this;
    }
}
