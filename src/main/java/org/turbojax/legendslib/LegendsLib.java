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
        AbilityRegistry.init();

        GlobalLoop.init(this);
        GlobalLoop.start();
    }

    public WeaponConfig getWeaponConfig() {
        return weaponConfig;
    }
}