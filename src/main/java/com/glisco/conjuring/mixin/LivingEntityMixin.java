package com.glisco.conjuring.mixin;

import com.glisco.conjuring.items.soul_alloy_tools.ArmorPiercingPlayerDamageSource;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyToolAbilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract void applyDamage(DamageSource source, float amount);

    private float damageReduction = 0;

    @Inject(method = "damage", at = @At("HEAD"))
    public void applySwordIgnorance(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.bypassesArmor()) return;
        if (!source.getName().equals("player")) return;
        if (!(source instanceof EntityDamageSource)) return;

        final PlayerEntity player = (PlayerEntity) source.getAttacker();
        if (!SoulAlloyToolAbilities.canArmorPierce(player)) return;

        damageReduction = SoulAlloyTool.getModifierLevel(player.getMainHandStack(), SoulAlloyTool.SoulAlloyModifier.IGNORANCE) * 0.1f * amount;
        applyDamage(new ArmorPiercingPlayerDamageSource(player), damageReduction);
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
    public void applySwordAoe(DamageSource source, float amount, CallbackInfo ci) {
        if (source.bypassesArmor()) return;
        if (!source.getName().equals("player")) return;
        if (!(source instanceof EntityDamageSource)) return;

        final PlayerEntity player = (PlayerEntity) source.getAttacker();

        if (!SoulAlloyToolAbilities.canAoeHit(player)) return;

        final int scopeLevel = SoulAlloyTool.getModifierLevel(player.getMainHandStack(), SoulAlloyTool.SoulAlloyModifier.SCOPE);
        final int range = 2 + scopeLevel;

        for (Entity entity : world.getOtherEntities(this, new Box(getPos().subtract(range, 1, range), getPos().add(range, 1, range)), entity -> entity instanceof LivingEntity)) {
            entity.damage(DamageSource.player(player), amount * 0.3f * scopeLevel);
        }
    }

    @ModifyVariable(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"), ordinal = 0)
    public float changeAmount(float amount) {
        return amount - damageReduction;
    }

}
