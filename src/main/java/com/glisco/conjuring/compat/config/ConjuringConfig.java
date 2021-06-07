package com.glisco.conjuring.compat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = "conjuring")
public class ConjuringConfig implements ConfigData {

    public ConjuringConfig(){
        conjurer_config.conjurer_blacklist.add("minecraft:wither");
        conjurer_config.conjurer_blacklist.add("minecraft:ender_dragon");
    }

    @ConfigEntry.Gui.CollapsibleObject
    public ConjurerConfig conjurer_config = new ConjurerConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public ToolsConfig tools_config = new ToolsConfig();

    public static class ConjurerConfig {
        @Comment("How many extra mobs to spawn per abundance charm")
        public int abundance_multiplier = 2;

        @Comment("How many ticks of waiting to remove per haste charm")
        public float haste_multiplier = 93.75f;

        @Comment("How many blocks of range to add per scope charm")
        public int scope_multiplier = 6;

        @Comment("How many extra mobs are allowed close to the conjurer per ignorance charm")
        public int ignorance_multiplier = 2;

        @Comment("Which mob types you shouldn't be able to create a conjuring focus for")
        public List<String> conjurer_blacklist = new ArrayList<>();
    }

    public static class ToolsConfig {

        @Comment("What percentage (additive) of the original damage to apply to each entity in the aoe per scope level")
        public float sword_scope_damage_multiplier = 0.3f;

        @Comment("How many entities the sword's aoe is allowed to affect")
        public int sword_scope_max_entities = 15;

        @Comment("What percentage (additive) of the original damage to make armor piercing per ignorance level")
        public float sword_ignorance_multiplier = 0.1f;

        @Comment("Exponent for the haste level, attack speed calculation goes like this: -2.4f + haste_level^exponent")
        public double sword_haste_exponent = 1.5d;

        @Comment("How much durability firing a projectile with sword costs")
        public int sword_secondary_durability_cost = 20;

        @Comment("Cooldown for firing projectiles with the sword, in ticks")
        public int sword_secondary_cooldown = 30;

        @Comment("How much of the sword's damage each fired projectile deals")
        public float sword_projectile_damage_multiplier = 0.2f;


        // <-- --- -->


        @Comment("The exponent for the scope level, tree cutting range is calculated like this: 8 + scope_level^exponent * 8")
        public double axe_scope_exponent = 3d;

        @Comment("How much durability firing a projectile with the hatchet costs, base value")
        public int axe_secondary_base_durability_cost = 10;

        @Comment("How much durability firing a projectile with the hatchet costs, added per scope level")
        public int axe_secondary_per_scope_durability_cost = 5;

        @Comment("Cooldown for firing projectiles with the hatchet, in ticks")
        public int axe_secondary_cooldown = 15;


        // <-- --- -->


        @Comment("How much durability firing a projectile with the pickaxe costs")
        public int pickaxe_secondary_durability_cost = 15;

        @Comment("Cooldown for firing projectiles with the pickaxe, in ticks")
        public int pickaxe_secondary_cooldown = 15;

        @Comment("How many ores the pickaxe's projectile can mine in one go")
        public int pickaxe_veinmine_max_blocks = 32;


        // <-- --- -->


        @Comment("From what distance the shovel's projectile pulls in items")
        public int shovel_magnet_range = 4;
    }

}
