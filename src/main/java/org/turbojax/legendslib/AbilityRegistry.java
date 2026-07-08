package org.turbojax.legendslib;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import org.turbojax.legendslib.event.AbilityRegistrationEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class AbilityRegistry {
    private static final Map<NamespacedKey,BiConsumer<Integer,Player>> INVENTORY_ABILITIES = new ConcurrentHashMap<>();
    private static final Map<NamespacedKey,BiConsumer<Integer,Player>> HELD_ABILITIES = new ConcurrentHashMap<>();
    private static final Map<NamespacedKey,BiConsumer<Integer,EntityDamageByEntityEvent>> ATTACKING_ABILITIES = new ConcurrentHashMap<>();
    private static final Map<NamespacedKey,BiConsumer<Integer,Player>> PRIMARY_ABILITIES = new ConcurrentHashMap<>();
    private static final Map<NamespacedKey,BiConsumer<Integer,Player>> SECONDARY_ABILITIES = new ConcurrentHashMap<>();

    private static boolean initialized = false;

    // Preventing instances of the Registry
    private AbilityRegistry() {}

    /**
     * Runs the registry initialization event.<br>
     *
     * This can only be run once.  Nothing will happen if it is run more than once.<br>
     * Not intended for external use.
     */
    @ApiStatus.Internal
    public static void init() {
        if (initialized) return;

        initialized = true;

        AbilityRegistrationEvent event = new AbilityRegistrationEvent();

        event.callEvent();

        INVENTORY_ABILITIES.putAll(event.getInventoryAbilities());
        HELD_ABILITIES.putAll(event.getHeldAbilities());
        ATTACKING_ABILITIES.putAll(event.getAttackingAbilities());
        PRIMARY_ABILITIES.putAll(event.getPrimaryAbilities());
        SECONDARY_ABILITIES.putAll(event.getSecondaryAbilities());
    }

    /** Retrieves an ability from the registry.  Returns null if no ability is found. */
    @Nullable
    public static BiConsumer<Integer,Player> getInventoryAbility(NamespacedKey key) {
        return INVENTORY_ABILITIES.get(key);
    }

    /** Retrieves an ability from the registry.  Returns null if no ability is found. */
    @Nullable
    public static BiConsumer<Integer,Player> getHeldAbility(NamespacedKey key) {
        return HELD_ABILITIES.get(key);
    }

    /** Retrieves an ability from the registry.  Returns null if no ability is found. */
    @Nullable
    public static BiConsumer<Integer,EntityDamageByEntityEvent> getAttackingAbility(NamespacedKey key) {
        return ATTACKING_ABILITIES.get(key);
    }

    /** Retrieves an ability from the registry.  Returns null if no ability is found. */
    @Nullable
    public static BiConsumer<Integer,Player> getPrimaryAbility(NamespacedKey key) {
        return PRIMARY_ABILITIES.get(key);
    }

    /** Retrieves an ability from the registry.  Returns null if no ability is found. */
    @Nullable
    public static BiConsumer<Integer,Player> getSecondaryAbility(NamespacedKey key) {
        return SECONDARY_ABILITIES.get(key);
    }

    @UnmodifiableView
    public static Map<NamespacedKey,BiConsumer<Integer,Player>> getInventoryAbilities() {
        return Map.copyOf(INVENTORY_ABILITIES);
    }

    @UnmodifiableView
    public static Map<NamespacedKey,BiConsumer<Integer,Player>> getHeldAbilities() {
        return Map.copyOf(HELD_ABILITIES);
    }

    @UnmodifiableView
    public static Map<NamespacedKey,BiConsumer<Integer,EntityDamageByEntityEvent>> getAttackingAbilities() {
        return Map.copyOf(ATTACKING_ABILITIES);
    }

    @UnmodifiableView
    public static Map<NamespacedKey,BiConsumer<Integer,Player>> getPrimaryAbilities() {
        return Map.copyOf(PRIMARY_ABILITIES);
    }

    @UnmodifiableView
    public static Map<NamespacedKey,BiConsumer<Integer,Player>> getSecondaryAbilities() {
        return Map.copyOf(SECONDARY_ABILITIES);
    }
}