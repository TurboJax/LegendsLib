package org.turbojax.legendslib;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        // Registering the main command
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,  commands -> {
            LegendsLibCommand llc = new LegendsLibCommand(this);

            commands.registrar().register(llc.build("legendslib"));
            commands.registrar().register(llc.build("ll"));
        });
    }

    public WeaponConfig getWeaponConfig() {
        return weaponConfig;
    }
}