package org.turbojax.legendslib;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

/** Utility class for ItemStacks that helps with managing the data stored in a LegendaryWeapon. */
@NullMarked
public class LegendaryWeapon {
    public static final NamespacedKey INVENTORY_ABILITIES_KEY = new NamespacedKey("legendslib", "inventory_abilities");
    public static final NamespacedKey HELD_ABILITIES_KEY = new NamespacedKey("legendslib", "held_abilities");
    public static final NamespacedKey ATTACKING_ABILITIES_KEY = new NamespacedKey("legendslib", "attacking_abilities");
    public static final NamespacedKey PRIMARY_ABILITIES_KEY = new NamespacedKey("legendslib", "primary_abilities");
    public static final NamespacedKey SECONDARY_ABILITIES_KEY = new NamespacedKey("legendslib", "secondary_abilities");
    public static final NamespacedKey KILL_COUNT_KEY = new NamespacedKey("legendslib", "kill_count");
    public static final NamespacedKey WEAPON_KEY = new NamespacedKey("legendslib", "weapon_key");

    public static boolean isLegendary(ItemStack item) {
        return item.getPersistentDataContainer().has(WEAPON_KEY);
    }

    public static List<NamespacedKey> getAbilities(AbilityType abilityType, ItemStack item) {
        if (!isLegendary(item)) return List.of();

        NamespacedKey key = switch (abilityType) {
            case INVENTORY -> INVENTORY_ABILITIES_KEY;
            case HELD -> HELD_ABILITIES_KEY;
            case ATTACKING -> ATTACKING_ABILITIES_KEY;
            case PRIMARY -> PRIMARY_ABILITIES_KEY;
            case SECONDARY -> SECONDARY_ABILITIES_KEY;
        };

        return WeaponConfig.deserializeAbilities(item.getPersistentDataContainer().get(key, PersistentDataType.STRING));
    }

    public static int getKillCount(ItemStack item, Player player) {
        // Making sure the item is a legendary weapon
        if (!isLegendary(item)) return -1;

        UUID uuid = player.getUniqueId();
        ItemMeta meta = item.getItemMeta();

        // Getting the kill count
        if (meta == null) return 0;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String serialized = pdc.getOrDefault(KILL_COUNT_KEY, PersistentDataType.STRING, "");

        String[] parts = serialized.split(";");
        for (String part : parts) {
            if (!part.startsWith(uuid.toString())) continue;

            return Integer.parseInt(part.substring(36));
        }

        return 0;
    }

    public static void setKillCount(ItemStack item, Player player, Integer count) {
        // Making sure the item is a legendary weapon
        if (!isLegendary(item)) return;

        UUID uuid = player.getUniqueId();

        // Getting the kill count
        item.editMeta(meta -> {
            if (meta == null) return;
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String serialized = pdc.getOrDefault(KILL_COUNT_KEY, PersistentDataType.STRING, "");

            String[] parts = serialized.split(";");
            for (int i = 0; i < parts.length; i++) {
                if (!parts[i].startsWith(uuid.toString())) continue;

                parts[i] = uuid.toString() + count;
            }

            pdc.set(KILL_COUNT_KEY, PersistentDataType.STRING, String.join(";", parts));
        });
    }

    public static void incrementKillCount(ItemStack item, Player player) {
        if (!isLegendary(item)) return;

        setKillCount(item, player, getKillCount(item, player) + 1);
    }

    public static void decrementKillCount(ItemStack item, Player player) {
        if (!isLegendary(item)) return;

        int count = getKillCount(item, player);
        if (count == 0) return;

        setKillCount(item, player, count - 1);
    }
}