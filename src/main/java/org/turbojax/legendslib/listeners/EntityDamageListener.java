package org.turbojax.legendslib.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.turbojax.legendslib.AbilityRegistry;
import org.turbojax.legendslib.AbilityType;
import org.turbojax.legendslib.LegendaryWeapon;

import java.util.Objects;

public class EntityDamageListener implements Listener {
    private final Plugin plugin;

    public EntityDamageListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Getting the attacking player
        if (!(event.getDamager() instanceof Player damager)) return;

        // Making sure the item is a legendary weapon
        ItemStack item = damager.getInventory().getItemInMainHand();
        if (!LegendaryWeapon.isLegendary(item)) return;

        // Activating attack abilities
        LegendaryWeapon.getAbilities(AbilityType.ATTACKING, item)
                .stream()
                .map(AbilityRegistry::getInventoryAbility)
                .filter(Objects::nonNull)
                .forEach(ability -> Bukkit.getScheduler().runTask(plugin, t -> ability.accept(LegendaryWeapon.getKillCount(item, damager), damager)));
    }
}
