package net.ellivers.whyamionfire.mixin.client;

import net.ellivers.whyamionfire.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;
import static net.minecraft.item.Items.MILK_BUCKET;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    private static boolean transparent;

    @Inject(method = "renderFireOverlay", cancellable = true, at = @At("HEAD"))
    private static void renderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
        transparent = false;
        ClientPlayerEntity player = minecraftClient.player;
        if (player != null) {
            if (minecraftClient.options.hudHidden || player.isCreative()) ci.cancel();
            else if (player.hasStatusEffect(FIRE_RESISTANCE)) {
                StatusEffectInstance effect = player.getStatusEffect(FIRE_RESISTANCE);
                assert effect != null;
                if (effect.getDuration() <= 200 || player.isHolding(MILK_BUCKET)) {
                    transparent = ModConfig.partialHide.equals(ModConfig.PartialHide.TRANSPARENT);
                    if (!transparent && effect.getDuration() % 20 < 10) ci.cancel();
                } else if (effect.getDuration() > 200) {
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
