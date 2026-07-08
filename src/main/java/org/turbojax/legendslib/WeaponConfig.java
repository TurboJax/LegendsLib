package org.turbojax.legendslib;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

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
        return config.contains(key);
    }

    public LegendaryWeapon getWeapon(String key) {
        LegendaryWeapon weapon = new LegendaryWeapon();

        weapon.setMaterial(getMaterial(key));
        weapon.setEnabled(getEnabled(key));
        weapon.setStackSize(getStackSize(key));
        weapon.setInventoryAbilities(getInventoryAbilities(key));
        weapon.setHeldAbilities(getHeldAbilities(key));
        weapon.setAttackingAbilities(getAttackingAbilities(key));
        weapon.setPrimaryAbilities(getPrimaryAbilities(key));
        weapon.setSecondaryAbilities(getSecondaryAbilities(key));
        weapon.setCmdFloats(getCmdFloats(key));
        weapon.setCmdStrings(getCmdStrings(key));
        weapon.setItemModel(getItemModel(key));

        return weapon;
    }

    public void saveWeapon(String key, LegendaryWeapon weapon) {
        setMaterial(key, weapon.getMaterial());
        setEnabled(key, weapon.getEnabled());
        setStackSize(key, weapon.getStackSize());
        setInventoryAbilities(key, weapon.getInventoryAbilities());
        setHeldAbilities(key, weapon.getHeldAbilities());
        setAttackingAbilities(key, weapon.getAttackingAbilities());
        setPrimaryAbilities(key, weapon.getPrimaryAbilities());
        setSecondaryAbilities(key, weapon.getSecondaryAbilities());
        setCmdFloats(key, weapon.getCmdFloats());
        setCmdStrings(key, weapon.getCmdStrings());
        setItemModel(key, weapon.getItemModel());
    }

    public @Nullable Material getMaterial(String key) {
        return Material.getMaterial(config.getString(key + ".material", ""));
    }

    public void setMaterial(String key, @Nullable Material material) {
        config.set(key + ".material", material);
    }

    public boolean getEnabled(String key) {
        return config.getBoolean(key + ".enabled");
    }

    public void setEnabled(String key, boolean enabled) {
        config.set(key + ".enabled", enabled);
    }

    public List<Float> getCmdFloats(String key) {
        return config.getFloatList(key + ".custom_model_data.floats");
    }

    public void setCmdFloats(String key, List<Float> cmdFloats) {
        config.set(key + ".custom_model_data.floats", cmdFloats);
    }

    public List<String> getCmdStrings(String key) {
        return config.getStringList(key + ".custom_model_data.strings");
    }

    public void setCmdStrings(String key, List<String> cmdStrings) {
        config.set(key + ".custom_model_data.strings", cmdStrings);
    }

    public @Nullable NamespacedKey getItemModel(String key) {
        return NamespacedKey.fromString(config.getString(key + ".item_model", ""));
    }

    public void setItemModel(String key, @Nullable NamespacedKey itemModel) {
        config.set(key + ".item_model", itemModel);
    }

    public int getStackSize(String key) {
        return config.getInt(key + ".stack_size", 1);
    }

    public void setStackSize(String key, int stackSize) {
        config.set(key + ".stack_size", stackSize);
    }

    public List<NamespacedKey> getInventoryAbilities(String key) {
        return config.getStringList(key + ".inventory_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
    }

    public void setInventoryAbilities(String key, List<NamespacedKey> inventoryAbilities) {
        config.set(key + ".inventory_abilities", inventoryAbilities.stream().map(NamespacedKey::toString).toList());
    }

    public List<NamespacedKey> getHeldAbilities(String key) {
        return config.getStringList(key + ".held_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
    }

    public void setHeldAbilities(String key, List<NamespacedKey> heldAbilities) {
        config.set(key + ".held_abilities", heldAbilities.stream().map(NamespacedKey::toString).toList());
    }

    public List<NamespacedKey> getAttackingAbilities(String key) {
        return config.getStringList(key + ".attacking_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
    }

    public void setAttackingAbilities(String key, List<NamespacedKey> attackingAbilities) {
        config.set(key + ".attacking_abilities", attackingAbilities.stream().map(NamespacedKey::toString).toList());
    }

    public List<NamespacedKey> getPrimaryAbilities(String key) {
        return config.getStringList(key + ".primary_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
    }

    public void setPrimaryAbilities(String key, List<NamespacedKey> primaryAbilities) {
        config.set(key + ".primary_abilities", primaryAbilities.stream().map(NamespacedKey::toString).toList());
    }

    public List<NamespacedKey> getSecondaryAbilities(String key) {
        return config.getStringList(key + ".secondary_abilities").stream().map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
    }

    public void setSecondaryAbilities(String key, List<NamespacedKey> secondaryAbilities) {
        config.set(key + ".secondary_abilities", secondaryAbilities.stream().map(NamespacedKey::toString).toList());
    }
}
