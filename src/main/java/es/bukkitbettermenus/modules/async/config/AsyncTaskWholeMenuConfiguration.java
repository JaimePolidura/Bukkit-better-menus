package es.bukkitbettermenus.modules.async.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class AsyncTaskWholeMenuConfiguration {
    @Getter private final Runnable runnable;
}
