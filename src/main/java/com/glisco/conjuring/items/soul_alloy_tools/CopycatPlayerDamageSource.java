package com.glisco.conjuring.items.soul_alloy_tools;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;

public class CopycatPlayerDamageSource extends DamageSource {

    private boolean pierceArmor = false;

    public CopycatPlayerDamageSource(PlayerEntity source) {
        super(source.getWorld().getDamageSources().playerAttack(source).getTypeRegistryEntry(), source);
    }

    public CopycatPlayerDamageSource pierceArmor() {
        this.pierceArmor = true;
        return this;
    }

    @Override
    public boolean isIn(TagKey<DamageType> tag) {
        if (this.pierceArmor) {
            if (tag == DamageTypeTags.BYPASSES_ARMOR) return true;
            if (tag == DamageTypeTags.BYPASSES_EFFECTS) return true;
        }

        return super.isIn(tag);
    }
}
