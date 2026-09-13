package com.github.drakescraft_labs.slimefun4.core.services;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;

import org.mockbukkit.mockbukkit.MockBukkit;

/**
 * The periodic block auto-save shares its lock with the dynamic batch drain. Losing that
 * lock must not drop the whole cycle, or the full pass and BlockStorage.saveChunks() are
 * postponed by a full interval each time the two tasks overlap.
 */
class TestAutoSavingRetry {

    private Slimefun plugin;

    @BeforeEach
    void load() {
        MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterEach
    void unload() {
        MockBukkit.unmock();
    }

    private AutoSavingService busyService() throws ReflectiveOperationException {
        AutoSavingService service = new AutoSavingService();

        Field pluginField = AutoSavingService.class.getDeclaredField("plugin");
        pluginField.setAccessible(true);
        pluginField.set(service, plugin);

        Field lock = AutoSavingService.class.getDeclaredField("blockPersistenceRunning");
        lock.setAccessible(true);
        ((AtomicBoolean) lock.get(service)).set(true);

        return service;
    }

    private void saveAllBlocksWithRetry(AutoSavingService service, int attempt) throws ReflectiveOperationException {
        Method method = AutoSavingService.class.getDeclaredMethod("saveAllBlocksWithRetry", int.class);
        method.setAccessible(true);
        method.invoke(service, attempt);
    }

    private int maxRetries(AutoSavingService service) throws ReflectiveOperationException {
        Field field = AutoSavingService.class.getDeclaredField("blockSaveMaxRetries");
        field.setAccessible(true);
        return field.getInt(service);
    }

    private int computeBlockSaveMaxRetries(int intervalMinutes) throws ReflectiveOperationException {
        Method method = AutoSavingService.class.getDeclaredMethod("computeBlockSaveMaxRetries", int.class);
        method.setAccessible(true);
        return (int) method.invoke(null, intervalMinutes);
    }

    @Test
    @DisplayName("A periodic block save that lost the lock reschedules itself")
    void testRetryIsScheduled() throws ReflectiveOperationException {
        AutoSavingService service = busyService();
        int before = plugin.getServer().getScheduler().getPendingTasks().size();

        saveAllBlocksWithRetry(service, 0);

        Assertions.assertTrue(plugin.getServer().getScheduler().getPendingTasks().size() > before,
            "saveAllBlocksWithRetry() dropped its cycle instead of scheduling a retry");
    }

    @Test
    @DisplayName("Retries of a periodic block save are bounded")
    void testRetriesAreBounded() throws ReflectiveOperationException {
        AutoSavingService service = busyService();
        int before = plugin.getServer().getScheduler().getPendingTasks().size();

        saveAllBlocksWithRetry(service, maxRetries(service));

        Assertions.assertEquals(before, plugin.getServer().getScheduler().getPendingTasks().size(),
            "saveAllBlocksWithRetry() kept retrying past its safety limit");
    }

    @Test
    @DisplayName("The retry budget spans the interval instead of a fixed 30 seconds")
    void testRetryBudgetFollowsInterval() throws ReflectiveOperationException {
        // 15 minutes of interval, minus the 30 s margin, at one retry per second.
        Assertions.assertEquals(870, computeBlockSaveMaxRetries(15),
            "the retry budget must cover the interval, not a fixed 30 s");
    }

    @Test
    @DisplayName("A tiny interval still keeps the minimum retry budget")
    void testRetryBudgetHasFloor() throws ReflectiveOperationException {
        Assertions.assertEquals(30, computeBlockSaveMaxRetries(1),
            "a short interval must not drop the retry budget below its floor");
        Assertions.assertEquals(30, computeBlockSaveMaxRetries(0),
            "a zero interval must not produce a negative retry budget");
    }
}
