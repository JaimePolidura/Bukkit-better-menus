package es.bukkitbettermenus.utils;

import java.util.concurrent.Callable;

public final class ExceptionUtils {
    public static void runOrTerminate(CheckedRunnable checkedRunnable) {
        try {
            checkedRunnable.run();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static <T> T runAndGetOrTerminate(Callable<T> callable) {
        try {
            return callable.call();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
