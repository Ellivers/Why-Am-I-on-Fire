package net.ellivers.whyamionfire.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;
import static net.minecraft.item.Items.MILK_BUCKET;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "renderFireOverlay", cancellable = true, at = @At("HEAD"))
    private static void renderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
        ClientPlayerEntity player = minecraftClient.player;
        if (player != null) {
            if (player.isCreative()) ci.cancel();
            else if (player.hasStatusEffect(FIRE_RESISTANCE)) {
                for (StatusEffectInstance effect : player.getStatusEffects()) {
                    if (effect.getEffectType() == FIRE_RESISTANCE) {
                        if (effect.getDuration() <= 200 || player.isHolding(MILK_BUCKET)) {
                            if (effect.getDuration() % 20 < 10) ci.cancel();
                        }
                        else if (effect.getDuration() > 200) {
                            ci.cancel();
                        }
                    }
                }
            }
        }
    }

}
