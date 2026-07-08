package org.turbojax.legendslib;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public class LegendaryWeapon {
    public static final NamespacedKey INVENTORY_ABILITIES_KEY = new NamespacedKey("legendslib", "inventory_abilities");
    public static final NamespacedKey HELD_ABILITIES_KEY = new NamespacedKey("legendslib", "held_abilities");
    public static final NamespacedKey ATTACKING_ABILITIES_KEY = new NamespacedKey("legendslib", "attacking_abilities");
    public static final NamespacedKey PRIMARY_ABILITIES_KEY = new NamespacedKey("legendslib", "primary_abilities");
    public static final NamespacedKey SECONDARY_ABILITIES_KEY = new NamespacedKey("legendslib", "secondary_abilities");
    public static final NamespacedKey KILL_COUNT_KEY = new NamespacedKey("legendslib", "kill_count");


    private @Nullable Material material;
    private boolean enabled;
    private List<Float> cmdFloats = List.of();
    private List<String> cmdStrings = List.of();
    private @Nullable NamespacedKey itemModel;
    private int stackSize;

    private List<NamespacedKey> inventoryAbilities = List.of();
    private List<NamespacedKey> heldAbilities = List.of();
    private List<NamespacedKey> attackingAbilities = List.of();
    private List<NamespacedKey> primaryAbilities = List.of();
    private List<NamespacedKey> secondaryAbilities = List.of();

    public @Nullable Material getMaterial() {
        return material;
    }

    public void setMaterial(@Nullable Material material) {
        this.material = material;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Float> getCmdFloats() {
        return cmdFloats;
    }

    public void setCmdFloats(List<Float> cmdFloats) {
        this.cmdFloats = cmdFloats;
    }

    public List<String> getCmdStrings() {
        return cmdStrings;
    }

    public void setCmdStrings(List<String> cmdStrings) {
        this.cmdStrings = cmdStrings;
    }

    public @Nullable NamespacedKey getItemModel() {
        return itemModel;
    }

    public void setItemModel(@Nullable NamespacedKey itemModel) {
        this.itemModel = itemModel;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    public List<NamespacedKey> getInventoryAbilities() {
        return inventoryAbilities;
    }

    public void setInventoryAbilities(List<NamespacedKey> inventoryAbilities) {
        this.inventoryAbilities = inventoryAbilities;
    }

    public List<NamespacedKey> getHeldAbilities() {
        return heldAbilities;
    }

    public void setHeldAbilities(List<NamespacedKey> heldAbilities) {
        this.heldAbilities = heldAbilities;
    }

    public List<NamespacedKey> getAttackingAbilities() {
        return attackingAbilities;
    }

    public void setAttackingAbilities(List<NamespacedKey> attackingAbilities) {
        this.attackingAbilities = attackingAbilities;
    }

    public List<NamespacedKey> getPrimaryAbilities() {
        return primaryAbilities;
    }

    public void setPrimaryAbilities(List<NamespacedKey> primaryAbilities) {
        this.primaryAbilities = primaryAbilities;
    }

    public List<NamespacedKey> getSecondaryAbilities() {
        return secondaryAbilities;
    }

    public void setSecondaryAbilities(List<NamespacedKey> secondaryAbilities) {
        this.secondaryAbilities = secondaryAbilities;
    }

    public ItemStack createItem() {
        if (material == null) throw new IllegalArgumentException("Weapons need to have a specified material.");

        ItemStack item = new ItemStack(material);

        // Editing the item metadata
        item.editMeta(meta -> {
            // Editing the custom model data
            CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            cmd.setFloats(cmdFloats);
            cmd.setStrings(cmdStrings);
            meta.setCustomModelDataComponent(cmd);

            // Editing the item model
            meta.setItemModel(itemModel);

            // Editing the stack size
            meta.setMaxStackSize(stackSize);

            // Storing the abilities in the pdc
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(INVENTORY_ABILITIES_KEY, PersistentDataType.STRING, serializeAbilities(inventoryAbilities));
            pdc.set(HELD_ABILITIES_KEY, PersistentDataType.STRING, serializeAbilities(heldAbilities));
            pdc.set(ATTACKING_ABILITIES_KEY, PersistentDataType.STRING, serializeAbilities(attackingAbilities));
            pdc.set(PRIMARY_ABILITIES_KEY, PersistentDataType.STRING, serializeAbilities(primaryAbilities));
            pdc.set(SECONDARY_ABILITIES_KEY, PersistentDataType.STRING, serializeAbilities(secondaryAbilities));
        });

        return item;
    }

    public static LegendaryWeapon deserialize(ItemStack item) {
        LegendaryWeapon weapon = new LegendaryWeapon();

        weapon.setMaterial(item.getType());
        weapon.setStackSize(item.getMaxStackSize());

        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        weapon.setInventoryAbilities(deserializeAbilities(pdc.get(INVENTORY_ABILITIES_KEY, PersistentDataType.STRING)));
        weapon.setHeldAbilities(deserializeAbilities(pdc.get(HELD_ABILITIES_KEY, PersistentDataType.STRING)));
        weapon.setAttackingAbilities(deserializeAbilities(pdc.get(ATTACKING_ABILITIES_KEY, PersistentDataType.STRING)));
        weapon.setPrimaryAbilities(deserializeAbilities(pdc.get(PRIMARY_ABILITIES_KEY, PersistentDataType.STRING)));
        weapon.setSecondaryAbilities(deserializeAbilities(pdc.get(SECONDARY_ABILITIES_KEY, PersistentDataType.STRING)));

        item.editMeta(meta -> {
            weapon.setItemModel(meta.getItemModel());

            weapon.setCmdFloats(meta.getCustomModelDataComponent().getFloats());
            weapon.setCmdStrings(meta.getCustomModelDataComponent().getStrings());
        });

        return weapon;
    }

    private static String serializeAbilities(List<NamespacedKey> abilities) {
        return abilities.stream().map(NamespacedKey::toString).collect(Collectors.joining(";"));
    }

    private static List<NamespacedKey> deserializeAbilities(@Nullable String abilities) {
        if (abilities == null) return List.of();

        return Stream.of(abilities.split(";")).map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
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

        return deserializeAbilities(item.getPersistentDataContainer().get(key, PersistentDataType.STRING));
    }

    public static boolean isLegendary(ItemStack item) {
        return item.getPersistentDataContainer().has(LegendaryWeapon.INVENTORY_ABILITIES_KEY);
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