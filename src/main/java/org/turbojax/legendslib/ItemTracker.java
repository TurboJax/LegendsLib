package org.turbojax.legendslib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ItemTracker {
    public final LegendsLib plugin;
    public final File file;
    public final FileConfiguration config = new YamlConfiguration();

    public ItemTracker(LegendsLib plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "tracker.yml");
    }

    /**
     * Reloads the configuration.
     *
     * @return Whether the configuration was loaded successfully.
     */
    public boolean load() {
        // Not doing anything if the plugin isn't enabled
        if (!plugin.isEnabled()) {
            LegendsLib.LOGGER.error("{} not loaded, cannot load {}.", plugin.getName(), file.getName());
            return false;
        }

        // Creating the file if it doesn't exist.
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(file.getName(), true);
        }

        // Loading the config
        try {
            config.load(file);
            LegendsLib.LOGGER.info("Successfully loaded {}", file.getName());
            return true;
        } catch (InvalidConfigurationException e) {
            LegendsLib.LOGGER.warn("{} contains an invalid YAML configuration.  Verify the contents of the file.", file.getName());
        } catch (IOException e) {
            LegendsLib.LOGGER.error("Could not find {}.  Check that it exists.", file.getName());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Writes the config to the file.
     *
     * @return Whether the config was successfully written.
     */
    public boolean save() {
        // Not doing anything if the plugin isn't enabled
        if (!plugin.isEnabled()) {
            LegendsLib.LOGGER.error("{} not loaded, cannot save {}.", plugin.getName(), file.getName());
            return false;
        }

        // Creating the file if it doesn't exist.
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }

        // Saving the config
        try {
            config.save(file);
            LegendsLib.LOGGER.info("Saved {}", file.getName());
            return true;
        } catch (IOException e) {
            LegendsLib.LOGGER.warn("Could not save {}.  Make sure the user has write permissions.", file.getName());
        }

        return false;
    }

    private int getStored(String key, String value, String weaponKey) {
        if (!config.contains(weaponKey)) return 0;

        List<Map<String,Object>> items = (List<Map<String, Object>>) config.getList(weaponKey);
        assert items != null;

        for (Map<String,Object> item : items) {
            if (!value.equals(item.get(key))) continue;

            return (Integer) item.get("count");
        }

        return 0;
    }

    private void setStored(String key, String value, String weaponKey, int count) {
        if (count < 0) count = 0;

        if (!config.contains(weaponKey)) {
            config.set(weaponKey, List.of(Map.of("count", count, key, value)));
            return;
        }

        List<Map<String,Object>> items = (List<Map<String, Object>>) config.getList(weaponKey);
        assert items != null;

        for (Map<String,Object> item : items) {
            if (!value.equals(item.get(key))) continue;

            item.put("count", count);
            return;
        }

        items.add(Map.of("count", count, key, value));

        config.set(weaponKey, items);
    }

    private void incrementStored(String key, String value, String weaponKey, int count) {
        setStored(key, value, weaponKey, getStored(key, value, weaponKey) + count);
    }

    private void decrementStored(String key, String value, String weaponKey, int count) {
        setStored(key, value, weaponKey, getStored(key, value, weaponKey) - count);
    }

    public int getStored(TrackerPlayerKey key, OfflinePlayer player, String weaponKey) {
        return getStored(key.name().toLowerCase(), player.getName(), weaponKey);
    }

    public void setStored(TrackerPlayerKey key, OfflinePlayer player, String weaponKey, int count) {
        setStored(key.name().toLowerCase(), player.getName(), weaponKey, count);
    }

    public void incrementStored(TrackerPlayerKey key, OfflinePlayer player, String weaponKey, int count) {
        incrementStored(key.name().toLowerCase(), player.getName(), weaponKey, count);
    }

    public void decrementStored(TrackerPlayerKey key, OfflinePlayer player, String weaponKey, int count) {
        decrementStored(key.name().toLowerCase(), player.getName(), weaponKey, count);
    }

    public int getStored(TrackerLocationKey key, Location location, String weaponKey) {
        return getStored(key.name().toLowerCase(), String.format("(%d, %d, %d, %s)", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()), weaponKey);
    }

    public void setStored(TrackerLocationKey key, Location location, String weaponKey, int count) {
        setStored(key.name().toLowerCase(), String.format("(%d, %d, %d, %s)", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()), weaponKey, count);
    }

    public void incrementStored(TrackerLocationKey key, Location location, String weaponKey, int count) {
        incrementStored(key.name().toLowerCase(), String.format("(%d, %d, %d, %s)", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()), weaponKey, count);
    }

    public void decrementStored(TrackerLocationKey key, Location location, String weaponKey, int count) {
        decrementStored(key.name().toLowerCase(), String.format("(%d, %d, %d, %s)", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()), weaponKey, count);
    }

    public enum TrackerPlayerKey {
        PLAYER,
        ECHEST
    }

    public enum TrackerLocationKey {
        ITEM_ENTITY,
        ENTITY_HOLDING_ITEM,
        STORAGE
    }
}
