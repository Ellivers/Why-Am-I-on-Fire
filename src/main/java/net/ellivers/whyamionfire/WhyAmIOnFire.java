package net.ellivers.whyamionfire;

import net.ellivers.whyamionfire.config.MidnightConfig;
import net.ellivers.whyamionfire.config.ModConfig;
import net.fabricmc.api.ModInitializer;

public class WhyAmIOnFire implements ModInitializer {
    public static final String MOD_ID = "whyamionfire";

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, ModConfig.class);
    }
}
