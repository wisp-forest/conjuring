package com.glisco.conjuring.items.soul_alloy_tools;

import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class CopycatPlayerDamageSource extends EntityDamageSource {

    public CopycatPlayerDamageSource(PlayerEntity source) {
        super("player", source);
    }

    public CopycatPlayerDamageSource pierceArmor() {
        setBypassesArmor();
        setUnblockable();
        return this;
    }
}
