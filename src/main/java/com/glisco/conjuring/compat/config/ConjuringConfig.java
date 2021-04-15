package com.glisco.conjuring.compat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "conjuring")
public class ConjuringConfig implements ConfigData {

    @Comment("How many extra mobs to spawn per plentifulness charm")
    public int plentifulness_multiplier = 2;

    @Comment("How many ticks of waiting to remove per haste charm")
    public float haste_multiplier = 93.75f;

    @Comment("How many blocks of range to add per scope charm")
    public int scope_multiplier = 6;

    @Comment("How many extra mobs are allowed close to the conjurer per ignorance charm")
    public int ignorance_multiplier = 2;

}
