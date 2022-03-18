package net.ellivers.whyamionfire.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;
import static net.minecraft.item.Items.MILK_BUCKET;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public abstract Box getBoundingBox();

    @Shadow private int fireTicks;

    @Inject(method = "setFireTicks", at = @At("TAIL"))
    private void setFireTicks(int ticks, CallbackInfo ci) {
        extinguishIfResistantToFire();
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void baseTick(CallbackInfo ci) {
        extinguishIfResistantToFire();
    }

    public void extinguishIfResistantToFire() {
        if (((Entity) (Object) this) instanceof LivingEntity) {
            boolean isCreative = false;
            boolean hasFireResistance = false;
            int remainingDuration = 0;
            if (((LivingEntity) (Object) this).hasStatusEffect(FIRE_RESISTANCE)) {
                remainingDuration = ((LivingEntity) (Object) this).getStatusEffect(FIRE_RESISTANCE).getDuration();
                hasFireResistance = true;
            }
            if (((Entity) (Object) this) instanceof PlayerEntity && ((PlayerEntity) (Object) this).isCreative()) isCreative = true;

            if (((Entity) (Object) this).getFireTicks() > 0 && hasFireResistance && !isCreative
                    && !(this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(0.001D)).anyMatch((blockStatex)
                                    -> blockStatex.isIn(BlockTags.FIRE) || blockStatex.isOf(Blocks.LAVA))
                                    && ((hasFireResistance && remainingDuration <= 200) || ((LivingEntity) (Object) this).isHolding(MILK_BUCKET))
                        )
            )
            {
                this.fireTicks = 0;
            }
            else if (isCreative) {
                this.fireTicks = 0;
            }

        }
    }
}
