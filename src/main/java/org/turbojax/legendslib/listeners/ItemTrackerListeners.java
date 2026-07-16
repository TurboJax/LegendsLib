package org.turbojax.legendslib.listeners;

import io.papermc.paper.event.entity.ItemTransportingEntityValidateTargetEvent;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.turbojax.legendslib.ItemTracker;
import org.turbojax.legendslib.ItemTracker.*;
import org.turbojax.legendslib.LegendaryWeapon;

public class ItemTrackerListeners implements Listener {
    private final ItemTracker tracker;

    public ItemTrackerListeners(ItemTracker tracker) {
        this.tracker = tracker;
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        event.getPlayer();

        event.getInventory();
    }

    public void onItemPickup(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (!LegendaryWeapon.isLegendary(item)) return;

        String key = LegendaryWeapon.getKey(item);
        int count = item.getAmount() - event.getRemaining();

        if (event.getEntity() instanceof Player player) {
            // Incrementing count for items stored in a player's inventory
            tracker.incrementStored(TrackerPlayerKey.PLAYER, player, key, count);
        } else {
            // Incrementing count for items stored in an entity's inventory
            tracker.incrementStored(TrackerLocationKey.ENTITY_HOLDING_ITEM, event.getEntity().getLocation(), key, count);
        }
    }

    public void onThisThing(ItemTransportingEntityValidateTargetEvent event) {
        if (event.getEntity() instanceof CopperGolem cg) {
            cg.getEquipment().getItem(EquipmentSlot.HAND);
            // thing
        }
    }

}
