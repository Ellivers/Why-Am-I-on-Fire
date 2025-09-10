package net.ellivers.whyamionfire.mixin.client;

import net.ellivers.whyamionfire.WhyAmIOnFire;
import net.ellivers.whyamionfire.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    @Unique
    private static boolean transparent;

    @Inject(method = "renderFireOverlay", cancellable = true, at = @At("HEAD"))
    private static void renderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        transparent = false;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity player = minecraftClient.player;
        if (player != null) {
            if (minecraftClient.options.hudHidden || player.isCreative()) ci.cancel();
            else if (player.hasStatusEffect(FIRE_RESISTANCE)) {
                if (ModConfig.partialHide.equals(ModConfig.PartialHide.NONE)) {
                    ci.cancel();
                    return;
                }
                int duration = player.getStatusEffect(FIRE_RESISTANCE).getDuration();
                boolean holdingMilk = player.isHolding(WhyAmIOnFire::itemClearsEffects);
                if (duration <= 200 /*10 seconds*/ || holdingMilk) {
                    transparent = ModConfig.partialHide.equals(ModConfig.PartialHide.TRANSPARENT);
                    if (!transparent && duration % 20 < 10) ci.cancel();
                } else {
                    ci.cancel();
                }
            }
        }
    }

    @Redirect(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"))
    private static VertexConsumer makeTransparent(VertexConsumer instance, float red, float green, float blue, float alpha) {
        return instance.color(red, green, blue, transparent ? 0.4F : alpha);
    }
}
