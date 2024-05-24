package net.ellivers.whyamionfire.mixin;

import net.ellivers.whyamionfire.config.ModConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ellivers.whyamionfire.WhyAmIOnFire.CLEARS_EFFECTS;
import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private World world;

    @Shadow public abstract Box getBoundingBox();

    @Shadow private int fireTicks;

    @Shadow public abstract boolean isOnFire();

    @Inject(method = "setFireTicks", at = @At("TAIL"))
    private void setFireTicks(int ticks, CallbackInfo ci) {
        extinguishIfResistantToFire();
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void baseTick(CallbackInfo ci) {
        if (this.isOnFire()) extinguishIfResistantToFire();
    }

    @Unique
    public void extinguishIfResistantToFire() {
        Entity entity = (Entity) (Object) this;

        if (ModConfig.extinguishMobs && entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof PlayerEntity player && player.isCreative()) {
                this.fireTicks = 0;
                return;
            }

            boolean hasFireResistance = false;
            int remainingDuration = 0;
            if (livingEntity.hasStatusEffect(FIRE_RESISTANCE)) {
                remainingDuration = livingEntity.getStatusEffect(FIRE_RESISTANCE).getDuration();
                hasFireResistance = true;
            }

            if (!(livingEntity instanceof PlayerEntity) && hasFireResistance) {
                this.fireTicks = 0;
                return;
            }

            /* Extinguish the player if they have fire resistance and are on fire
            * But, if the player is standing in something that will set them on fire and:
            * their fire resistance is about to wear off, or
            * they are holding a bucket of milk (which can remove the fire resistance),
            * then don't extinguish the fire, as a warning.
             */
            if (livingEntity.getFireTicks() > 0 && hasFireResistance
                    && !(this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(0.001D)).anyMatch((blockState)
                                    -> blockState.isIn(BlockTags.FIRE) || blockState.isOf(Blocks.LAVA))
                                    && (remainingDuration <= 200 || (livingEntity.isHolding(item -> item.isIn(CLEARS_EFFECTS)))
                        )
            ))
            {
                this.fireTicks = 0;
            }

        }
    }
}
