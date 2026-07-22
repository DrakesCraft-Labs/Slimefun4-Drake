package org.mockbukkit.mockbukkit.plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Test-only replacement for MockBukkit's loader while its Paper 1.21.11
 * implementation initializes a real Maven {@code LibraryLoader} for events.
 */
public class MockBukkitPluginLoader implements PluginLoader {

    @Override
    public @NotNull Plugin loadPlugin(@NotNull File file) throws UnknownDependencyException {
        throw new UnsupportedOperationException("MockBukkit cannot load plugin files in unit tests");
    }

    @Override
    public @NotNull PluginDescriptionFile getPluginDescription(@NotNull File file) {
        throw new UnsupportedOperationException("MockBukkit cannot inspect plugin files in unit tests");
    }

    @Override
    public @NotNull Pattern[] getPluginFileFilters() {
        return new Pattern[0];
    }

    @Override
    public @NotNull Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
        @NotNull Listener listener,
        @NotNull Plugin plugin
    ) {
        Map<Class<? extends Event>, Set<RegisteredListener>> registrations = new HashMap<>();
        Set<Method> methods = new HashSet<>();
        for (Method method : listener.getClass().getMethods()) {
            methods.add(method);
        }
        for (Method method : listener.getClass().getDeclaredMethods()) {
            methods.add(method);
        }

        for (Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);
            if (handler == null || method.isBridge() || method.isSynthetic()) {
                continue;
            }

            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1 || !Event.class.isAssignableFrom(parameters[0])) {
                plugin.getLogger().severe("Invalid event handler: " + method.toGenericString());
                continue;
            }

            Class<? extends Event> eventClass = parameters[0].asSubclass(Event.class);
            method.setAccessible(true);
            EventExecutor executor = (registeredListener, event) -> invokeHandler(
                method,
                registeredListener,
                eventClass,
                event
            );
            registrations.computeIfAbsent(eventClass, ignored -> new HashSet<>())
                .add(new RegisteredListener(
                    listener,
                    executor,
                    handler.priority(),
                    plugin,
                    handler.ignoreCancelled()
                ));
        }
        return registrations;
    }

    private static void invokeHandler(
        Method method,
        Listener listener,
        Class<? extends Event> eventClass,
        Event event
    ) throws EventException {
        if (!eventClass.isAssignableFrom(event.getClass())) {
            return;
        }

        try {
            method.invoke(listener, event);
        } catch (InvocationTargetException exception) {
            throw new EventException(exception.getCause());
        } catch (ReflectiveOperationException | RuntimeException exception) {
            throw new EventException(exception);
        }
    }

    @Override
    public void enablePlugin(@NotNull Plugin plugin) {
        throw new UnsupportedOperationException("MockBukkit controls plugin enablement directly");
    }

    @Override
    public void disablePlugin(@NotNull Plugin plugin) {
        ((JavaPlugin) plugin).setEnabled(false);
        plugin.getServer().getPluginManager().callEvent(new PluginDisableEvent(plugin));
    }
}
