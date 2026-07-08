package org.turbojax.legendslib;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LegendsLib extends JavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("LegendsLib");

    @Override
    public void onEnable() {
        AbilityRegistry.init();

        GlobalLoop.init(this);
        GlobalLoop.start();
    }
}