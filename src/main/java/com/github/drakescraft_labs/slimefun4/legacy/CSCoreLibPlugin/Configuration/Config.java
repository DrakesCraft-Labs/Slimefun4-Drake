package me.mrCookieSlime.CSCoreLibPlugin.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.drakescraft_labs.slimefun4.legacy.api.BlockStorage;

/**
 * An old remnant of CS-CoreLib.
 * This will be removed once we updated everything.
 * Don't look at the code, it will be gone soon, don't worry.
 * Only used by the legacy {@link BlockStorage} system.
 */
public class Config {

    private static final Logger LOGGER = Logger.getLogger("Slimefun");

    /**
     * Guarda toda la lectura y escritura del {@link FileConfiguration} interno.
     * El hilo principal muta estos Config al colocar o romper bloques, mientras
     * el guardado automatico los recorre desde un hilo asincrono; sin este lock
     * la iteracion revienta con {@link java.util.ConcurrentModificationException}
     * y los cambios encolados se pierden.
     */
    private final Object lock = new Object();

    private final File file;
    private FileConfiguration config;

    /**
     * Creates a new Config Object for the specified File
     *
     * @param file
     *            The File for which the Config object is created for
     */
    public Config(File file) {
        this(file, YamlConfiguration.loadConfiguration(file));
    }

    /**
     * Creates a new Config Object for the specified File and FileConfiguration
     *
     * @param file
     *            The File to save to
     * @param config
     *            The FileConfiguration
     */
    public Config(File file, FileConfiguration config) {
        this.file = file;
        this.config = config;
    }

    /**
     * Creates a new Config Object for the File with in
     * the specified Location
     *
     * @param path
     *            The Path of the File which the Config object is created for
     */
    public Config(String path) {
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Returns the File the Config is handling
     *
     * @return The File this Config is handling
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Converts this Config Object into a plain FileConfiguration Object
     *
     * @return The converted FileConfiguration Object
     */
    public FileConfiguration getConfiguration() {
        return this.config;
    }

    /**
     * Sets the Value for the specified Path
     *
     * @param path
     *            The path in the Config File
     * @param value
     *            The Value for that Path
     */
    public void setValue(String path, Object value) {
        synchronized (lock) {
            this.config.set(path, value);
        }
    }

    /**
     * Saves the Config Object to its File
     */
    public void save() {
        save(this.file);
    }

    /**
     * Saves the Config Object to a File
     * 
     * @param file
     *            The File you are saving this Config to
     */
    public void save(File file) {
        // Serializa bajo el lock, pero deja la escritura en disco fuera de el:
        // asi el hilo principal nunca espera por I/O al guardar un bloque.
        String data;

        synchronized (lock) {
            data = config.saveToString();
        }

        try {
            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            Files.write(file.toPath(), data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Tragar este fallo perdia datos en silencio: al menos queda rastro.
            LOGGER.log(Level.WARNING, e, () -> "No se pudo guardar el archivo \"" + file.getName() + '"');
        }
    }

    /**
     * Sets the Value for the specified Path
     * (IF the Path does not yet exist)
     *
     * @param path
     *            The path in the Config File
     * @param value
     *            The Value for that Path
     */
    public void setDefaultValue(String path, Object value) {
        if (!contains(path)) {
            setValue(path, value);
        }
    }

    /**
     * Checks whether the Config contains the specified Path
     *
     * @param path
     *            The path in the Config File
     * @return True/false
     */
    public boolean contains(String path) {
        synchronized (lock) {
            return config.contains(path);
        }
    }

    /**
     * Returns the Object at the specified Path
     *
     * @param path
     *            The path in the Config File
     * @return The Value at that Path
     */
    public Object getValue(String path) {
        synchronized (lock) {
            return config.get(path);
        }
    }

    /**
     * Returns the String at the specified Path
     *
     * @param path
     *            The path in the Config File
     * @return The String at that Path
     */
    public String getString(String path) {
        synchronized (lock) {
            return config.getString(path);
        }
    }

    /**
     * Recreates the File of this Config
     */
    public void createFile() {
        try {
            this.file.createNewFile();
        } catch (IOException e) {}
    }

    /**
     * Returns all Paths in this Config
     *
     * @return All Paths in this Config
     */
    public Set<String> getKeys() {
        synchronized (lock) {
            // getKeys(false) construye un Set nuevo, por eso es seguro devolverlo.
            return config.getKeys(false);
        }
    }

    /**
     * Returns all Sub-Paths in this Config
     *
     * @param path
     *            The path in the Config File
     * @return All Sub-Paths of the specified Path
     */
    public Set<String> getKeys(String path) {
        synchronized (lock) {
            ConfigurationSection section = config.getConfigurationSection(path);
            return section == null ? Collections.emptySet() : section.getKeys(false);
        }
    }

    /**
     * Reloads the Configuration File
     */
    public void reload() {
        FileConfiguration reloaded = YamlConfiguration.loadConfiguration(this.file);

        synchronized (lock) {
            this.config = reloaded;
        }
    }
}
