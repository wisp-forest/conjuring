package com.glisco.conjuringforgery.items.soul_alloy_tools;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityDamageSource;

public class CopycatPlayerDamageSource extends EntityDamageSource {

    public CopycatPlayerDamageSource(PlayerEntity source) {
        super("player", source);
    }

    public CopycatPlayerDamageSource pierceArmor() {
        setDamageBypassesArmor();
        setDamageIsAbsolute();
        return this;
    }
}
