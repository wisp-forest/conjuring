package com.glisco.conjuringforgery.blocks;

import com.glisco.conjuringforgery.ConjuringForgery;
import com.glisco.conjuringforgery.WorldHelper;
import com.glisco.conjuringforgery.items.ConjuringFocus;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootSerializers;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SoulFunnelTileEntity extends TileEntity implements ITickableTileEntity {

    private ItemStack item;
    private float itemHeight = 0;
    private int slownessCooldown = 0;

    private int ritualTick = 0;
    private boolean ritualRunning = false;
    private UUID ritualEntity = null;
    private float particleOffset = 0;
    private float ritualStability = 0.1f;
    private final List<BlockPos> pedestalPositions;

    public SoulFunnelTileEntity() {
        super(ConjuringForgery.SOUL_FUNNEL_TILE.get());
        pedestalPositions = new ArrayList<>();
    }


    //Data Logic
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        CompoundNBT item;
        if (this.item != null) {
            item = this.item.serializeNBT();
            tag.put("Item", item);
        }
        tag.putInt("Cooldown", slownessCooldown);

        if (ritualRunning) {
            CompoundNBT ritual = new CompoundNBT();
            ritual.putInt("Tick", ritualTick);
            ritual.putUniqueId("Entity", ritualEntity);
            ritual.putFloat("ParticleOffset", particleOffset);
            ritual.putFloat("Stability", ritualStability);
            tag.put("Ritual", ritual);
        }

        ListNBT pedestals = new ListNBT();
        for (BlockPos p : pedestalPositions) {
            pedestals.add(new IntArrayNBT(new int[]{p.getX(), p.getY(), p.getZ()}));
        }
        tag.put("Pedestals", pedestals);

        return tag;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);

        CompoundNBT item = tag.getCompound("Item");
        this.item = null;
        if (!item.isEmpty()) {
            this.item = ItemStack.read(tag.getCompound("Item"));
        }

        ListNBT pedestals = tag.getList("Pedestals", 11);
        pedestalPositions.clear();
        for (INBT pedestal : pedestals) {
            int[] intPos = ((IntArrayNBT) pedestal).getIntArray();
            pedestalPositions.add(new BlockPos(intPos[0], intPos[1], intPos[2]));
        }

        slownessCooldown = tag.getInt("Cooldown");

        if (tag.contains("Ritual")) {
            ritualRunning = true;

            CompoundNBT ritual = tag.getCompound("Ritual");
            ritualEntity = ritual.getUniqueId("Entity");
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
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(null, pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 0, this.write(new CompoundNBT()));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    //Tick Logic
    @Override
    public void tick() {
        //Ritual tick logic
        if (ritualRunning) {
            ritualTick++;

            if (ritualTick == 1) {

                if (world.isRemote) {
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1, false);
                } else {
                    MobEntity e = (MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity);

                    particleOffset = e.getHeight() / 2;
                    this.markDirty();

                    e.teleportKeepLoaded(pos.getX() + 0.5f, e.getPosY(), pos.getZ() + 0.5f);
                    e.setMotion(0, 0.075f, 0);
                    e.setNoGravity(true);
                    calculateStability();
                }

            } else if (ritualTick == 20) {

                if (!world.isRemote) {
                    MobEntity e = (MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity);
                    if (verifyRitualEntity()) {
                        e.setMotion(0, 0, 0);
                        e.setNoAI(true);
                    }
                }

            } else if (ritualTick > 20 && ritualTick <= 80) {

                if (world.isRemote) {
                    for (BlockPos pos : pedestalPositions) {
                        if (!(world.getTileEntity(pos) instanceof BlackstonePedestalTileEntity)) continue;
                        if (!((BlackstonePedestalTileEntity) world.getTileEntity(pos)).isActive()) continue;

                        BlockPos p = pos.add(0, 1, 0);
                        BlockPos pVector = pos.subtract(this.pos);

                        IParticleData particle = new BlockParticleData(ParticleTypes.BLOCK, world.getBlockState(pos));
                        for (int i = 0; i < 4; i++) {
                            WorldHelper.spawnParticle(particle, world, p, 0.5f, 0.25f, 0.5f, 0.1f);
                        }

                        WorldHelper.spawnParticle(ParticleTypes.SOUL, world, p, 0.5f, 0.3f, 0.5f, pVector.getX() * -0.05f, 0.075f * particleOffset, pVector.getZ() * -0.05f, 0.1f);
                    }
                    for (int i = 0; i < 5; i++)
                        WorldHelper.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, world, pos, 0.5f, 1.75f + particleOffset, 0.5f, 0, -0.5f, 0f, 0.25f);
                } else {
                    if (ritualTick % 10 == 0) {
                        if (verifyRitualEntity()) {
                            MobEntity e = (MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity);
                            e.attackEntityFrom(DamageSource.OUT_OF_WORLD, 0.01f);
                        }
                    }
                }

            } else if (ritualTick > 80) {

                if (!world.isRemote()) {
                    if (verifyRitualEntity()) {
                        MobEntity e = (MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity);

                        int data = e.world.rand.nextDouble() < ritualStability ? 0 : 1;

                        world.playEvent(9005, e.getPosition(), data);
                        world.playEvent(9007, e.getPosition(), data);
                        world.setBlockState(pos, world.getBlockState(pos).with(SoulFunnelBlock.FILLED, false));

                        ItemStack drop = data == 0 ? ConjuringFocus.create(e.getType()) : new ItemStack(ConjuringForgery.CONJURING_FOCUS.get());
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), drop);

                        disablePedestals();
                        e.onKillCommand();

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

        if (!world.isRemote()) {
            if (slownessCooldown == 0 && this.getItem() != null) {
                if (world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty()) return;

                Entity e = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).get(0);
                if (e instanceof PlayerEntity || e instanceof EnderDragonEntity || e instanceof WitherEntity || !(e instanceof LivingEntity) || e.getTags().contains("affected"))
                    return;

                ((LivingEntity) e).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 15 * 20, 20));
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

    public void startRitual(UUID ritualEntity) {
        this.ritualRunning = true;
        this.ritualEntity = ritualEntity;
        this.markDirty();
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
        for (BlockPos p : pedestalPositions) {
            TileEntity blockEntity = world.getTileEntity(p);
            if (!(blockEntity instanceof BlackstonePedestalTileEntity)) continue;

            ((BlackstonePedestalTileEntity) blockEntity).setActive(false);
            ((BlackstonePedestalTileEntity) blockEntity).setRenderedItem(null);
        }
    }

    public boolean addPedestal(BlockPos pedestal) {
        if (pedestalPositions.size() >= 4) return false;

        if (!pedestalPositions.contains(pedestal)) pedestalPositions.add(pedestal);
        if (world.isRemote) {
            BlockPos offset = pedestal.subtract(pos);

            float offsetX = 0.5f + offset.getX() / 8f;
            float offsetY = 0.35f;
            float offsetZ = 0.5f + offset.getZ() / 8f;

            for (int i = 0; i < 20; i++) {
                WorldHelper.spawnParticle(ParticleTypes.WITCH, world, pos, offsetX, offsetY, offsetZ, 0, 0, 0, offset.getZ() / 12f, 0.1f, offset.getX() / 12f);
            }
        }
        this.markDirty();
        return true;
    }

    public boolean removePedestal(BlockPos pedestal, boolean pedestalActive) {
        boolean returnValue = pedestalPositions.remove(pedestal);
        this.markDirty();

        BlockPos offset = pedestal.subtract(pos);
        if (offset.getX() != 0) {
            world.playEvent(9010, pos, offset.getX());
        } else {
            world.playEvent(9011, pos, offset.getZ());
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
        Gson GSON = LootSerializers.func_237388_c_().create();

        JsonObject tableJSON = GSON.toJsonTree(table).getAsJsonObject();
        List<Item> drops = new ArrayList<>();

        try {
            for (JsonElement poolElement : tableJSON.get("pools").getAsJsonArray()) {

                JsonObject pool = poolElement.getAsJsonObject();
                JsonArray entries = pool.get("entries").getAsJsonArray();

                for (JsonElement entryElement : entries) {

                    JsonObject entry = entryElement.getAsJsonObject();

                    drops.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.get("name").getAsString())));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drops;
    }

    public void calculateStability() {
        ritualStability += world.getBiome(pos).getRegistryName().toString().equalsIgnoreCase("soul_sand_valley") ? 0.1f : 0f;

        List<Item> drops = extractDrops(world.getServer().getLootTableManager().getLootTableFromLocation(((MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity)).getLootTableResourceLocation()));

        for (BlockPos p : pedestalPositions) {
            if (!(world.getTileEntity(p) instanceof BlackstonePedestalTileEntity)) continue;
            BlackstonePedestalTileEntity pedestal = (BlackstonePedestalTileEntity) world.getTileEntity(p);

            if (pedestal.getRenderedItem() == null) continue;
            Item pedestalItem = pedestal.getRenderedItem().getItem();
            if (!drops.contains(pedestalItem)) continue;

            ritualStability += 0.2f;

            pedestal.setActive(true);
        }
        this.markDirty();
    }

    public void onBroken() {
        if (item != null)
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY() + 1.25, pos.getZ(), new ItemStack(ConjuringForgery.CONJURING_FOCUS.get()));

        if (ritualRunning) {
            MobEntity e = (MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity);

            world.playEvent(9005, e.getPosition(), 1);
            world.playEvent(9007, e.getPosition(), 1);

            disablePedestals();
            for (BlockPos pos : pedestalPositions) {
                if (!(world.getTileEntity(pos) instanceof BlackstonePedestalTileEntity)) continue;
                ((BlackstonePedestalTileEntity) world.getTileEntity(pos)).setLinkedFunnel(null);
            }

            e.onKillCommand();
        }
    }

    public void cancelRitual(boolean clearFlags) {
        world.playEvent(9005, pos.add(0, 2, 0), 1);
        world.playEvent(9007, pos.add(0, 2, 0), 1);

        disablePedestals();
        for (BlockPos pos : pedestalPositions) {
            if (!(world.getTileEntity(pos) instanceof BlackstonePedestalTileEntity)) continue;
            ((BlackstonePedestalTileEntity) world.getTileEntity(pos)).setLinkedFunnel(null);
        }

        MobEntity e = (MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity);
        if (e != null) e.onKillCommand();

        if (clearFlags) {
            ritualRunning = false;
            ritualEntity = null;
            ritualTick = 0;
            markDirty();
        }
    }

    private boolean verifyRitualEntity() {
        MobEntity e = (MobEntity) ((ServerWorld) world).getEntityByUuid(ritualEntity);
        if (e == null) {
            cancelRitual(true);
            return false;
        }

        if (e.getShouldBeDead()) {
            cancelRitual(true);
            return false;
        }

        return true;
    }

}
