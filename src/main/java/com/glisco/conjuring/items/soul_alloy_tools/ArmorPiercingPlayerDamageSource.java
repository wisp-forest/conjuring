package com.glisco.conjuring.items.soul_alloy_tools;

import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class ArmorPiercingPlayerDamageSource extends EntityDamageSource {

    public ArmorPiercingPlayerDamageSource(PlayerEntity source) {
        super("player", source);
        setBypassesArmor();
    }

}
