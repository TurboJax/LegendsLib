package org.turbojax.legendslib.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.turbojax.legendslib.LegendaryWeapon;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Entity entity = event.getDamageSource().getDirectEntity();

        // Handling if the entity isn't a player
        if (!(entity instanceof Player)) {
            entity = event.getDamageSource().getCausingEntity();

            // Handling if the entity still isn't a player
            if (!(entity instanceof Player)) return;
        }

        Player player = (Player) entity;

        // Getting the item
        ItemStack item = player.getInventory().getItemInMainHand();

        // Making sure the item is a legendary weapon
        if (!LegendaryWeapon.isLegendary(item)) return;

        // Increasing the kill count
        LegendaryWeapon.incrementKillCount(item, player);
    }
}
