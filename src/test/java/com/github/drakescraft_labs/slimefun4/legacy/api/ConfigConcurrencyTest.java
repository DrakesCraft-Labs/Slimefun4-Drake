package com.github.drakescraft_labs.slimefun4.legacy.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;

/**
 * Regresion del ticket 204: el guardado automatico recorre un {@link Config}
 * desde un hilo asincrono mientras el hilo principal sigue colocando bloques.
 * Sin sincronizacion interna eso lanzaba ConcurrentModificationException y los
 * cambios encolados de BlockStorage se perdian.
 */
class ConfigConcurrencyTest {

    @TempDir
    Path folder;

    @Test
    void readsAndWritesFromDifferentThreadsDoNotExplode() throws InterruptedException {
        Config config = new Config(new File(folder.toFile(), "world_nether.sfb"));

        for (int i = 0; i < 200; i++) {
            config.setValue("x" + i, "SLIMEFUN_MACHINE");
        }

        List<Throwable> failures = new CopyOnWriteArrayList<>();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);

        Thread writer = new Thread(() -> {
            try {
                start.await();

                for (int i = 200; i < 4000; i++) {
                    config.setValue("x" + i, "SLIMEFUN_MACHINE");
                }
            } catch (Throwable e) {
                failures.add(e);
            } finally {
                done.countDown();
            }
        });

        Thread saver = new Thread(() -> {
            try {
                start.await();

                for (int i = 0; i < 400; i++) {
                    assertTrue(config.getKeys().size() >= 200);
                    config.save(new File(folder.toFile(), "world_nether.sfb.tmp"));
                }
            } catch (Throwable e) {
                failures.add(e);
            } finally {
                done.countDown();
            }
        });

        writer.start();
        saver.start();
        start.countDown();

        assertTrue(done.await(60, TimeUnit.SECONDS), "los hilos no terminaron a tiempo");
        assertEquals(Collections.emptyList(), failures, "el acceso concurrente no debe lanzar excepciones");
        assertEquals(4000, config.getKeys().size());
    }

    @Test
    void missingSectionReturnsEmptyKeysInsteadOfFailing() {
        Config config = new Config(new File(folder.toFile(), "vacio.sfb"));

        assertTrue(config.getKeys("no-existe").isEmpty());
    }

    @Test
    void savedFileCanBeReloaded() {
        File file = new File(folder.toFile(), "persistido.sfb");
        Config config = new Config(file);

        config.setValue("maquina", "ELECTRIC_SMELTERY");
        config.save();

        assertTrue(file.exists());
        assertEquals("ELECTRIC_SMELTERY", new Config(file).getString("maquina"));
    }
}
