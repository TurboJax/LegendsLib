package org.turbojax.legendslib;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class WeaponConfig {
    public final Plugin plugin;
    public final File file;
    public final FileConfiguration config = new YamlConfiguration();

    public WeaponConfig(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "weapons.yml");
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

    public boolean hasWeapon(String key) {
        String material = config.getString(key + ".material");

        if (material == null) return false;

        if (Material.getMaterial(material) == null) {
            LegendsLib.LOGGER.warn("Invalid material '{}' for weapon '{}'", material, key);
            return false;
        }

        return true;
    }

    public LegendaryWeapon getWeapon(String key) {
        Material material = Material.getMaterial(config.getString(key + ".material", ""));
        assert material != null : "Invalid material for weapon \"" + key + "\"";

        int stackSize = config.getInt(key + ".stack_size", 1);
        List<NamespacedKey> inventoryAbilities = config.getStringList(key + ".inventory_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
        List<NamespacedKey> heldAbilities = config.getStringList(key + ".held_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
        List<NamespacedKey> attackingAbilities = config.getStringList(key + ".attacking_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
        List<NamespacedKey> primaryAbilities = config.getStringList(key + ".primary_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
        List<NamespacedKey> secondaryAbilities = config.getStringList(key + ".secondary_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
        List<Float> cmdFloats = config.getFloatList(key + ".custom_model_data.floats");
        List<String> cmdStrings = config.getStringList(key + ".custom_model_data.strings");
        NamespacedKey itemModel = NamespacedKey.fromString(config.getString(key + ".item_model", ""));

        return new LegendaryWeapon(material, cmdFloats, cmdStrings, itemModel, stackSize, inventoryAbilities, heldAbilities, attackingAbilities, primaryAbilities, secondaryAbilities);
    }

    public void saveWeapon(String key, LegendaryWeapon weapon) {
        config.set(key + ".material", weapon.material().toString());
        config.set(key + ".stack_size", weapon.stackSize());
        config.set(key + ".inventory_abilities", weapon.inventoryAbilities().stream().map(NamespacedKey::toString).toList());
        config.set(key + ".held_abilities", weapon.heldAbilities().stream().map(NamespacedKey::toString).toList());
        config.set(key + ".attacking_abilities", weapon.attackingAbilities().stream().map(NamespacedKey::toString).toList());
        config.set(key + ".primary_abilities", weapon.primaryAbilities().stream().map(NamespacedKey::toString).toList());
        config.set(key + ".secondary_abilities", weapon.secondaryAbilities().stream().map(NamespacedKey::toString).toList());
        config.set(key + ".custom_model_data.floats", weapon.cmdFloats());
        config.set(key + ".custom_model_data.strings", weapon.cmdStrings());
        config.set(key + ".item_model", weapon.itemModel());
    }
}
