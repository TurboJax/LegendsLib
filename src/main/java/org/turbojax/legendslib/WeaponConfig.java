package org.turbojax.legendslib;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Objects;

public class WeaponConfig {
    FileConfiguration config = new YamlConfiguration();

    LegendaryWeapon getWeapon(String key) {
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
}
