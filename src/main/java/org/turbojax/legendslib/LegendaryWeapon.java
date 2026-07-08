package org.turbojax.legendslib;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public record LegendaryWeapon(Material material, List<Float> cmdFloats, List<String> cmdStrings,
                              @Nullable NamespacedKey itemModel, int stackSize, List<NamespacedKey> inventoryAbilities,
                              List<NamespacedKey> heldAbilities, List<NamespacedKey> attackingAbilities,
                              List<NamespacedKey> primaryAbilities, List<NamespacedKey> secondaryAbilities) {

    public static final NamespacedKey INVENTORY_ABILITIES_KEY = new NamespacedKey("legendslib", "inventory_abilities");
    public static final NamespacedKey HELD_ABILITIES_KEY = new NamespacedKey("legendslib", "held_abilities");
    public static final NamespacedKey ATTACKING_ABILITIES_KEY = new NamespacedKey("legendslib", "attacking_abilities");
    public static final NamespacedKey PRIMARY_ABILITIES_KEY = new NamespacedKey("legendslib", "primary_abilities");
    public static final NamespacedKey SECONDARY_ABILITIES_KEY = new NamespacedKey("legendslib", "secondary_abilities");

    public ItemStack createItem() {
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
        Material material = item.getType();
        int stackSize = item.getMaxStackSize();

        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        List<NamespacedKey> inventoryAbilities = deserializeAbilities(pdc.get(INVENTORY_ABILITIES_KEY, PersistentDataType.STRING));
        List<NamespacedKey> heldAbilities = deserializeAbilities(pdc.get(HELD_ABILITIES_KEY, PersistentDataType.STRING));
        List<NamespacedKey> attackingAbilities = deserializeAbilities(pdc.get(ATTACKING_ABILITIES_KEY, PersistentDataType.STRING));
        List<NamespacedKey> primaryAbilities = deserializeAbilities(pdc.get(PRIMARY_ABILITIES_KEY, PersistentDataType.STRING));
        List<NamespacedKey> secondaryAbilities = deserializeAbilities(pdc.get(SECONDARY_ABILITIES_KEY, PersistentDataType.STRING));

        List<Float> cmdFloats = new ArrayList<>();
        List<String> cmdStrings = new ArrayList<>();
        AtomicReference<@Nullable NamespacedKey> itemModel = new AtomicReference<>();

        item.editMeta(meta -> {
            itemModel.set(meta.getItemModel());

            cmdFloats.addAll(meta.getCustomModelDataComponent().getFloats());
            cmdStrings.addAll(meta.getCustomModelDataComponent().getStrings());
        });

        return new LegendaryWeapon(material, cmdFloats, cmdStrings, itemModel.get(), stackSize, inventoryAbilities, heldAbilities, attackingAbilities, primaryAbilities, secondaryAbilities);
    }

    private static String serializeAbilities(List<NamespacedKey> abilities) {
        return abilities.stream().map(NamespacedKey::asString).collect(Collectors.joining(";"));
    }
    private static List<NamespacedKey> deserializeAbilities(@Nullable String abilities) {
        if (abilities == null) return List.of();

        return Stream.of(abilities.split(";")).map(NamespacedKey::fromString).filter(Objects::nonNull).toList();
    }
}