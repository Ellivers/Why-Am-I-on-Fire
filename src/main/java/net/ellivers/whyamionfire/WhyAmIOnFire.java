package net.ellivers.whyamionfire;

import net.ellivers.whyamionfire.config.MidnightConfig;
import net.ellivers.whyamionfire.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class WhyAmIOnFire implements ModInitializer {
    public static final String MOD_ID = "whyamionfire";
    public static TagKey<Item> CLEARS_EFFECTS = TagKey.of(RegistryKeys.ITEM, new Identifier("whyamionfire","clears_effects"));

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, ModConfig.class);
    }
}
