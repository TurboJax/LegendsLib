package org.turbojax.legendslib;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.turbojax.legendslib.listeners.EntityDamageListener;
import org.turbojax.legendslib.listeners.PlayerDeathListener;

public final class LegendsLib extends JavaPlugin {
    public static final Logger LOGGER = LoggerFactory.getLogger("LegendsLib");

    private final WeaponConfig weaponConfig;

    public LegendsLib() {
        this.weaponConfig = new WeaponConfig(this);
    }

    @Override
    public void onEnable() {
        // Kicking up the registry
        AbilityRegistry.init();

        // Starting the global loop
        GlobalLoop.init(this);
        GlobalLoop.start();

        // Loading the weapon config
        weaponConfig.load();

        // Registering the main command
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,  commands -> {
            LegendsLibCommand llc = new LegendsLibCommand(this);

            commands.registrar().register(llc.build("legendslib"));
            commands.registrar().register(llc.build("ll"));
        });

        // Registering event listeners
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
    }

    public WeaponConfig getWeaponConfig() {
        return weaponConfig;
    }
}