package com.glisco.conjuring.blocks;

import com.glisco.conjuring.ConjuringCommon;
import com.glisco.conjuring.items.ConjuringFocus;
import com.glisco.owo.particles.ClientParticles;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class SoulFunnelBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RitualCore {

    private ItemStack item;
    private float itemHeight = 0;
    private int slownessCooldown = 0;

    private int ritualTick = 0;
    private boolean ritualRunning = false;
    private UUID ritualEntity = null;
    private float particleOffset = 0;
    private float ritualStability = 0.1f;
    private final List<BlockPos> pedestalPositions;

    public SoulFunnelBlockEntity(BlockPos pos, BlockState state) {
        super(ConjuringCommon.SOUL_FUNNEL_BLOCK_ENTITY, pos, state);
        pedestalPositions = new ArrayList<>();
    }


    //Data Logic
    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        NbtCompound item = new NbtCompound();
        if (this.item != null) {
            this.item.writeNbt(item);
        }
        tag.put("Item", item);
        tag.putInt("Cooldown", slownessCooldown);

        if (ritualRunning) {
            NbtCompound ritual = new NbtCompound();
            ritual.putInt("Tick", ritualTick);
            ritual.putUuid("Entity", ritualEntity);
            ritual.putFloat("ParticleOffset", particleOffset);
            ritual.putFloat("Stability", ritualStability);
            tag.put("Ritual", ritual);
        }

        savePedestals(tag, pedestalPositions);

        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        NbtCompound item = tag.getCompound("Item");
        this.item = null;
        if (!item.isEmpty()) {
            this.item = ItemStack.fromNbt(tag.getCompound("Item"));
        }

        loadPedestals(tag, pedestalPositions);

        slownessCooldown = tag.getInt("Cooldown");

        if (tag.contains("Ritual")) {
            ritualRunning = true;

            NbtCompound ritual = tag.getCompound("Ritual");
            ritualEntity = ritual.getUuid("Entity");
            ritualTick = ritual.getInt("Tick");
            particleOffset = ritual.getFloat("ParticleOffset");
            ritualStability = ritual.getFloat("Stability");
        } else {
            ritualRunning = false;
            ritualEntity = null;
            ritualTick = 0;
            particleOffset = 0;
            ritualStability = 0.1f;
        }
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return this.writeNbt(tag);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (this.world instanceof ServerWorld) {
            this.sync();
        }
    }

    public static void ticker(World world, BlockPos pos, BlockState state, SoulFunnelBlockEntity funnel) {
        funnel.tick();
    }

    //Tick Logic
    public void tick() {
        //Ritual tick logic
        if (ritualRunning) {
            ritualTick++;

            if (ritualTick == 1) {

                if (world.isClient) {
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1, false);
                } else {
                    MobEntity ritualEntity = (MobEntity) ((ServerWorld) world).getEntity(this.ritualEntity);

                    particleOffset = ritualEntity.getHeight() / 2;
                    this.markDirty();

                    ritualEntity.teleport(pos.getX() + 0.5f, ritualEntity.getY(), pos.getZ() + 0.5f);
                    ritualEntity.setVelocity(0, 0.075f, 0);
                    ritualEntity.setNoGravity(true);
                    calculateStability();

                }

            } else if (ritualTick == 20) {

                if (!world.isClient) {

                    MobEntity e = (MobEntity) ((ServerWorld) world).getEntity(this.ritualEntity);
                    if (verifyRitualEntity()) {
                        e.setVelocity(0, 0, 0);
                        e.setAiDisabled(true);
                        final Vec3d entityPos = Vec3d.of(pos).add(0.5, 1.85, 0.5);
                        e.setPos(entityPos.x, entityPos.y, entityPos.z);
                    }
                }

            } else if (ritualTick > 20 && ritualTick <= 80) {

                if (world.isClient) {
                    for (BlockPos pos : pedestalPositions) {
                        if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
                        if (!((BlackstonePedestalBlockEntity) world.getBlockEntity(pos)).isActive()) continue;

                        BlockPos p = pos.add(0, 1, 0);
                        BlockPos pVector = pos.subtract(this.pos);

                        ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(pos));
                        ClientParticles.setParticleCount(4);
                        ClientParticles.spawnWithOffsetFromBlock(particle, world, p, new Vec3d(0.5, 0.25, 0.5), 0.1);

                        ClientParticles.setVelocity(new Vec3d(pVector.getX() * -0.05, particleOffset * 0.075, pVector.getZ() * -0.05));
                        ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL, world, p, new Vec3d(0.5, 0.3, 0.5), 0.1);
                    }

                    ClientParticles.setParticleCount(5);
                    ClientParticles.setVelocity(new Vec3d(0, -0.5, 0));
                    ClientParticles.spawnWithOffsetFromBlock(ParticleTypes.SOUL_FIRE_FLAME, world, pos, new Vec3d(0.5, 1.75 + particleOffset, 0.5), 0.1);

                } else {
                    if (ritualTick % 10 == 0) {
                        if (verifyRitualEntity()) {
                            MobEntity e = (MobEntity) ((ServerWorld) world).getEntity(ritualEntity);
                            e.damage(DamageSource.OUT_OF_WORLD, 0.01f);
                        }
                    }
                }

            } else if (ritualTick > 80) {

                if (!world.isClient()) {
                    if (verifyRitualEntity()) {
                        MobEntity e = (MobEntity) ((ServerWorld) world).getEntity(ritualEntity);

                        int data = e.world.random.nextDouble() < ritualStability ? 0 : 1;

                        world.syncWorldEvent(9005, e.getBlockPos(), data);
                        world.syncWorldEvent(9007, e.getBlockPos(), data);
                        world.setBlockState(pos, world.getBlockState(pos).with(SoulFunnelBlock.FILLED, false));

                        ItemStack drop = data == 0 ? ConjuringFocus.writeData(item, e.getType()) : item;
                        ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), drop);

                        disablePedestals();
                        e.kill();

                        this.item = null;
                        this.ritualEntity = null;
                        this.ritualTick = 0;
                        this.ritualRunning = false;
                        this.ritualStability = 0.1f;
                    }
                }
                this.markDirty();
            }
        }

        //Item bouncing and slowness logic
        itemHeight = itemHeight >= 100 ? 0 : itemHeight + 1;
        if (slownessCooldown > 0) slownessCooldown--;

        if (!world.isClient()) {
            if (slownessCooldown == 0 && this.getItem() != null) {
                if (world.getOtherEntities(null, new Box(pos)).isEmpty()) return;

                Entity e = world.getOtherEntities(null, new Box(pos)).get(0);
                if (e instanceof PlayerEntity || e instanceof EnderDragonEntity || e instanceof WitherEntity || !(e instanceof LivingEntity) || e.getScoreboardTags().contains("affected"))
                    return;

                ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 15 * 20, 20));
                slownessCooldown = 30 * 20;
                this.markDirty();
            }
        }
    }

    //Actual Logic
    public void setItem(@Nullable ItemStack item) {
        this.item = item == null ? null : item.copy();
        this.markDirty();
    }

    @Nullable
    public ItemStack getItem() {
        if (item == null) {
            return null;
        }
        return item.copy();
    }

    public boolean tryStartRitual(PlayerEntity player) {

        if (item == null) return false;

        if (world.getOtherEntities(player, new Box(pos, pos.add(1, 3, 1))).isEmpty()) return false;
        Entity e = world.getOtherEntities(player, new Box(pos, pos.add(1, 3, 1))).get(0);

        if (e instanceof ItemEntity item && item.getStack().isOf(ConjuringCommon.DISTILLED_SPIRIT) && this.item != null) {

            final MobEntity newEntity = (MobEntity) EntityType.loadEntityWithPassengers(this.item.getTag().getCompound("Entity"), world, Function.identity());

            if (newEntity == null) return false;

            final var health = newEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            health.setBaseValue(health.getBaseValue() * 1.5);
            newEntity.setHealth(newEntity.getMaxHealth());

            final var attackDamage = newEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attackDamage != null) attackDamage.setBaseValue(attackDamage.getBaseValue() * 1.5);

            newEntity.updatePosition(pos.getX(), pos.getY() + 1, pos.getZ());
            world.spawnEntity(newEntity);

            world.syncWorldEvent(9005, pos.add(0, 1, 0), 1);
            world.syncWorldEvent(9007, pos.add(0, 1, 0), 1);

            ItemScatterer.spawn(world, pos.getX(), pos.getY() + 0.75, pos.getZ(), new ItemStack(this.item.getItem()));

            item.discard();
            this.item = null;
            markDirty();
            return true;
        }

        if (!(e instanceof MobEntity) || ConjuringCommon.CONFIG.conjurer_config.conjurer_blacklist.contains(Registry.ENTITY_TYPE.getId(e.getType()).toString()))
            return false;

        if (item.getTag().contains("Enitity")) return false;

        if (!world.isClient()) {
            this.ritualRunning = true;
            this.ritualEntity = e.getUuid();
            this.markDirty();

            ConjuringCommon.EXTRACTION_RITUAL_CRITERION.trigger((ServerPlayerEntity) player);
        }

        return true;
    }

    public boolean isRitualRunning() {
        return ritualRunning;
    }

    public float getItemHeight() {
        return (float) Math.sin(2 * Math.PI * itemHeight / 100) / 25f;
    }

    public boolean onCooldown() {
        return slownessCooldown > 0;
    }

    private void disablePedestals() {
        for (BlockPos pos : pedestalPositions) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof BlackstonePedestalBlockEntity)) continue;

            ((BlackstonePedestalBlockEntity) blockEntity).setActive(false);
            ((BlackstonePedestalBlockEntity) blockEntity).setItem(ItemStack.EMPTY);
        }
    }

    public boolean linkPedestal(BlockPos pedestal) {
        if (pedestalPositions.size() >= 4) return false;

        if (!pedestalPositions.contains(pedestal)) pedestalPositions.add(pedestal);
        if (world.isClient) {
            BlockPos offset = pedestal.subtract(pos);

            float offsetX = 0.5f + offset.getX() / 8f;
            float offsetY = 0.35f;
            float offsetZ = 0.5f + offset.getZ() / 8f;

            ClientParticles.setParticleCount(20);
            ClientParticles.spawnPrecise(ParticleTypes.WITCH, world, new Vec3d(offsetX, offsetY, offsetZ).add(Vec3d.of(pos)), offset.getZ() / 12d, 0.1f, offset.getX() / 12d);
        }
        this.markDirty();
        return true;
    }

    public boolean removePedestal(BlockPos pedestal, boolean pedestalActive) {
        boolean returnValue = pedestalPositions.remove(pedestal);
        this.markDirty();

        BlockPos offset = pedestal.subtract(pos);
        if (offset.getX() != 0) {
            world.syncWorldEvent(9010, pos, offset.getX());
        } else {
            world.syncWorldEvent(9011, pos, offset.getZ());
        }

        if (this.ritualRunning && pedestalActive) {
            this.ritualStability = 0f;
            this.ritualTick = 81;
            this.markDirty();
        }

        return returnValue;
    }

    public List<BlockPos> getPedestalPositions() {
        return new ArrayList<>(pedestalPositions);
    }

    public List<Item> extractDrops(LootTable table) {
        Gson GSON = LootGsons.getTableGsonBuilder().create();

        JsonObject tableJSON = GSON.toJsonTree(table).getAsJsonObject();
        List<Item> drops = new ArrayList<>();

        try {
            for (JsonElement poolElement : tableJSON.get("pools").getAsJsonArray()) {

                JsonObject pool = poolElement.getAsJsonObject();
                JsonArray entries = pool.get("entries").getAsJsonArray();

                for (JsonElement entryElement : entries) {

                    JsonObject entry = entryElement.getAsJsonObject();

                    drops.add(Registry.ITEM.get(new Identifier(entry.get("name").getAsString())));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drops;
    }

    public void calculateStability() {
        ritualStability += world.getServer().getRegistryManager().get(Registry.BIOME_KEY).getId(world.getBiome(pos)).getPath().equalsIgnoreCase("soul_sand_valley") ? 0.1f : 0f;

        List<Item> drops = extractDrops(world.getServer().getLootManager().getTable(((MobEntity) ((ServerWorld) world).getEntity(ritualEntity)).getLootTable()));

        for (BlockPos p : pedestalPositions) {
            if (!(world.getBlockEntity(p) instanceof BlackstonePedestalBlockEntity)) continue;
            BlackstonePedestalBlockEntity pedestal = (BlackstonePedestalBlockEntity) world.getBlockEntity(p);

            if (pedestal.getItem().isEmpty()) continue;
            Item pedestalItem = pedestal.getItem().getItem();
            if (!drops.contains(pedestalItem)) continue;

            ritualStability += 0.2f;
            pedestal.setActive(true);
        }
        this.markDirty();
    }

    public void onBroken() {
        if (item != null)
            ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), item);

        if (ritualRunning) {
            cancelRitual(false);
        }
    }

    public void cancelRitual(boolean clearFlags) {
        world.syncWorldEvent(9005, pos.add(0, 2, 0), 1);
        world.syncWorldEvent(9007, pos.add(0, 2, 0), 1);

        disablePedestals();
        for (BlockPos pos : pedestalPositions) {
            if (!(world.getBlockEntity(pos) instanceof BlackstonePedestalBlockEntity)) continue;
            ((BlackstonePedestalBlockEntity) world.getBlockEntity(pos)).setLinkedFunnel(null);
        }

        MobEntity e = (MobEntity) ((ServerWorld) world).getEntity(ritualEntity);
        if (e != null) e.kill();

        if (clearFlags) {
            ritualRunning = false;
            ritualEntity = null;
            ritualTick = 0;
            markDirty();
        }
    }

    private boolean verifyRitualEntity() {

        MobEntity e = (MobEntity) ((ServerWorld) world).getEntity(ritualEntity);
        if (e == null) {
            cancelRitual(true);
            return false;
        }

        if (e.isDead()) {
            cancelRitual(true);
            return false;
        }

        return true;
    }
}
