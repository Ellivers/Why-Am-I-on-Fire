package net.ellivers.whyamionfire.mixin;

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

    @Inject(method = "setFireTicks", at = @At("TAIL"))
    private void setFireTicks(int ticks, CallbackInfo ci) {
        extinguishIfResistantToFire();
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void baseTick(CallbackInfo ci) {
        if (((Entity) (Object) this).isOnFire()) extinguishIfResistantToFire();
    }

    @Unique
    public void extinguishIfResistantToFire() {
        if (((Entity) (Object) this) instanceof LivingEntity) {
            if (((Entity) (Object) this) instanceof PlayerEntity && ((PlayerEntity) (Object) this).isCreative()) {
                this.fireTicks = 0;
                return;
            }
            if (!(((Entity) (Object) this) instanceof PlayerEntity)) {
                this.fireTicks = 0;
                return;
            }

            boolean hasFireResistance = false;
            int remainingDuration = 0;
            if (((LivingEntity) (Object) this).hasStatusEffect(FIRE_RESISTANCE)) {
                remainingDuration = ((LivingEntity) (Object) this).getStatusEffect(FIRE_RESISTANCE).getDuration();
                hasFireResistance = true;
            }

            /* Extinguish the player if they have fire resistance and are on fire
            * But, if the player is standing in something that will set them on fire and:
            * their fire resistance is about to wear off, or
            * they are holding a bucket of milk (which can remove the fire resistance),
            * then don't extinguish the fire, as a warning.
             */
            if (((Entity) (Object) this).getFireTicks() > 0 && hasFireResistance
                    && !(this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(0.001D)).anyMatch((blockState)
                                    -> blockState.isIn(BlockTags.FIRE) || blockState.isOf(Blocks.LAVA))
                                    && (remainingDuration <= 200 || ((LivingEntity) (Object) this).isHolding(item -> item.isIn(CLEARS_EFFECTS)))
                        )
            )
            {
                this.fireTicks = 0;
            }

        }
    }
}
