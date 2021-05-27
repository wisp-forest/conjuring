package com.glisco.conjuringforgery;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class GemTinkeringCriterion extends AbstractCriterionTrigger<GemTinkeringCriterion.Conditions> {

    public static final ResourceLocation ID = new ResourceLocation("conjuring", "gem_tinkering");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Conditions deserializeTrigger(JsonObject obj, EntityPredicate.AndPredicate playerPredicate, ConditionArrayParser predicateDeserializer) {
        return new Conditions(ID, playerPredicate);
    }

    public void trigger(ServerPlayerEntity player) {
        this.triggerListeners(player, conditions -> true);
    }

    public static class Conditions extends CriterionInstance {

        public Conditions(ResourceLocation id, EntityPredicate.AndPredicate playerPredicate) {
            super(id, playerPredicate);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer predicateSerializer) {
            return new JsonObject();
        }
    }
}
