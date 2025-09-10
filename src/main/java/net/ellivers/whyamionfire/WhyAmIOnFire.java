package net.ellivers.whyamionfire;

import net.ellivers.whyamionfire.config.MidnightConfig;
import net.ellivers.whyamionfire.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;

public class WhyAmIOnFire implements ModInitializer {
    public static final String MOD_ID = "whyamionfire";

    public static boolean itemClearsEffects(ItemStack item) {
        ConsumableComponent component = item.getComponents().get(DataComponentTypes.CONSUMABLE);
        if (component == null) return false;
        return component.onConsumeEffects().contains(ClearAllEffectsConsumeEffect.INSTANCE);
    }

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, ModConfig.class);
    }
}
