package org.turbojax.legendslib;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@ApiStatus.Internal
public class GlobalLoop implements Consumer<ScheduledTask> {
    private static final GlobalLoop INSTANCE = new GlobalLoop();

    private static boolean running = false;
    private static Plugin plugin;

    private GlobalLoop() {}

    public static void init(Plugin plugin) {
        GlobalLoop.plugin = plugin;
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, INSTANCE, 0, 1, TimeUnit.SECONDS);
    }

    public static void start() {
        running = true;
    }

    public static void stop() {
        running = false;
    }

    @Override
    public void accept(ScheduledTask task) {
        if (!running) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : p.getInventory()) {
                // Skipping empty slots
                if (item == null) continue;

                // Skipping non-legendary items
                if (!LegendaryWeapon.isLegendary(item)) continue;

                // Executing inventory abilities
                LegendaryWeapon.getAbilities(AbilityType.INVENTORY, item)
                        .stream()
                        .map(AbilityRegistry::getInventoryAbility)
                        .filter(Objects::nonNull)
                        .forEach(ability -> Bukkit.getScheduler().runTask(plugin, t -> ability.accept(item, p)));
            }

            // Executing held item abilities in the player's main hand
            ItemStack mainItem = p.getInventory().getItemInMainHand();
            LegendaryWeapon.getAbilities(AbilityType.HELD, mainItem)
                    .stream()
                    .map(AbilityRegistry::getInventoryAbility)
                    .filter(Objects::nonNull)
                    .forEach(ability -> Bukkit.getScheduler().runTask(plugin, t -> ability.accept(mainItem, p)));

            // Executing held item abilities for items in the player's offhand
            ItemStack offItem = p.getInventory().getItemInOffHand();
            LegendaryWeapon.getAbilities(AbilityType.HELD, offItem)
                    .stream()
                    .map(AbilityRegistry::getInventoryAbility)
                    .filter(Objects::nonNull)
                    .forEach(ability -> Bukkit.getScheduler().runTask(plugin, t -> ability.accept(offItem, p)));
        }
    }
}
