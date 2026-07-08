package org.turbojax.legendslib.event;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AbilityRegistrationEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Map<NamespacedKey,Runnable> inventoryAbilities = new ConcurrentHashMap<>();
    private final Map<NamespacedKey,Runnable> heldAbilities = new ConcurrentHashMap<>();
    private final Map<NamespacedKey,Consumer<EntityDamageByEntityEvent>> attackingAbilities = new ConcurrentHashMap<>();
    private final Map<NamespacedKey,BiConsumer<Integer,Player>> primaryAbilities = new ConcurrentHashMap<>();
    private final Map<NamespacedKey,BiConsumer<Integer,Player>> secondaryAbilities = new ConcurrentHashMap<>();

    @ApiStatus.Internal
    public AbilityRegistrationEvent() {}

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public boolean registerInventoryAbility(NamespacedKey key, Runnable runnable) {
        if (inventoryAbilities.containsKey(key)) return false;

        inventoryAbilities.put(key, runnable);

        return true;
    }

    public boolean registerHeldAbility(NamespacedKey key, Runnable runnable) {
        if (heldAbilities.containsKey(key)) return false;

        heldAbilities.put(key, runnable);

        return true;
    }

    public boolean registerAttackingAbility(NamespacedKey key, Consumer<EntityDamageByEntityEvent> runnable) {
        if (attackingAbilities.containsKey(key)) return false;

        attackingAbilities.put(key, runnable);

        return true;
    }

    public boolean registerPrimaryAbility(NamespacedKey key, BiConsumer<Integer,Player> runnable) {
        if (primaryAbilities.containsKey(key)) return false;

        primaryAbilities.put(key, runnable);

        return true;
    }

    public boolean registerSecondaryAbility(NamespacedKey key, BiConsumer<Integer,Player> runnable) {
        if (secondaryAbilities.containsKey(key)) return false;

        secondaryAbilities.put(key, runnable);

        return true;
    }

    @UnmodifiableView
    public Map<NamespacedKey,Runnable> getInventoryAbilities() {
        return Map.copyOf(inventoryAbilities);
    }

    @UnmodifiableView
    public Map<NamespacedKey,Runnable> getHeldAbilities() {
        return Map.copyOf(heldAbilities);
    }

    @UnmodifiableView
    public Map<NamespacedKey,Consumer<EntityDamageByEntityEvent>> getAttackingAbilities() {
        return Map.copyOf(attackingAbilities);
    }

    @UnmodifiableView
    public Map<NamespacedKey,BiConsumer<Integer,Player>> getPrimaryAbilities() {
        return Map.copyOf(primaryAbilities);
    }

    @UnmodifiableView
    public Map<NamespacedKey,BiConsumer<Integer,Player>> getSecondaryAbilities() {
        return Map.copyOf(secondaryAbilities);
    }
}
