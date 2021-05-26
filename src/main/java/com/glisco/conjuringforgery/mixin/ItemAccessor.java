package com.glisco.conjuringforgery.mixin;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Item.class)
public interface ItemAccessor {

    @Accessor("ATTACK_SPEED_MODIFIER")
    static UUID getAttackSpeedModifierID() {
        throw new AssertionError();
    }
}
