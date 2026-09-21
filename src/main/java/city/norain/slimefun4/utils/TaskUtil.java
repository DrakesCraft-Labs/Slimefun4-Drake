package city.norain.slimefun4.utils;

import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class TaskUtil {
    @SneakyThrows
    public void runSyncMethod(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Slimefun.runSync(runnable);
        }
    }

    @SneakyThrows
    public <T> T runSyncMethod(Callable<T> callable) {
        if (Bukkit.isPrimaryThread()) {
            return callable.call();
        } else {
            try {
                return Bukkit.getScheduler()
                        .callSyncMethod(Slimefun.instance(), callable)
                        .get(1, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                Slimefun.logger().log(Level.WARNING, "Timeout when executing sync method", e);
                return null;
            }
        }
    }
}
