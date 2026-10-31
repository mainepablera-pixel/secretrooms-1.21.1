package com.wynprice.secretrooms.client.world;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;

public class DummyIWorld implements LevelAccessor {
   private final LevelAccessor world;

   public DummyIWorld(LevelAccessor world) {
      this.world = world;
   }

   @Nullable
   public BlockEntity m_7702_(BlockPos pos) {
      return (BlockEntity)SecretBaseBlock.getMirrorData(this.world, pos).map(SecretData::getTileEntityCache).orElseGet(() -> this.world.m_7702_(pos));
   }

   public BlockState m_8055_(BlockPos pos) {
      return (BlockState)SecretBaseBlock.getMirrorState(this.world, pos).orElseGet(() -> this.world.m_8055_(pos));
   }

   public FluidState m_6425_(BlockPos pos) {
      return this.m_8055_(pos).m_60819_();
   }

   public boolean m_6933_(BlockPos pos, BlockState state, int flags, int recursionLeft) {
      Optional<SecretData> data = SecretBaseBlock.getMirrorData(this.world, pos);
      if (data.isPresent()) {
         ((SecretData)data.get()).setBlockState(state);
         return true;
      } else {
         return this.world.m_6933_(pos, state, flags, recursionLeft);
      }
   }

   public boolean m_7471_(BlockPos p_46951_, boolean p_46952_) {
      return false;
   }

   public boolean m_7740_(BlockPos p_46957_, boolean p_46958_, Entity p_46959_, int p_46960_) {
      return false;
   }

   public long m_183596_() {
      return this.world.m_183596_();
   }

   public LevelTickAccess<Block> m_183326_() {
      return this.world.m_183326_();
   }

   public LevelTickAccess<Fluid> m_183324_() {
      return this.world.m_183324_();
   }

   public LevelData m_6106_() {
      return this.world.m_6106_();
   }

   public DifficultyInstance m_6436_(BlockPos p_46800_) {
      return this.world.m_6436_(p_46800_);
   }

   public @org.jetbrains.annotations.Nullable MinecraftServer m_7654_() {
      return this.world.m_7654_();
   }

   public ChunkSource m_7726_() {
      return this.world.m_7726_();
   }

   public RandomSource m_213780_() {
      return this.world.m_213780_();
   }

   public void m_5594_(@org.jetbrains.annotations.Nullable Player p_46775_, BlockPos p_46776_, SoundEvent p_46777_, SoundSource p_46778_, float p_46779_, float p_46780_) {
      this.world.m_5594_(p_46775_, p_46776_, p_46777_, p_46778_, p_46779_, p_46780_);
   }

   public void m_7106_(ParticleOptions p_46783_, double p_46784_, double p_46785_, double p_46786_, double p_46787_, double p_46788_, double p_46789_) {
      this.world.m_7106_(p_46783_, p_46784_, p_46785_, p_46786_, p_46787_, p_46788_, p_46789_);
   }

   public void m_5898_(@org.jetbrains.annotations.Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
      this.world.m_5898_(p_46771_, p_46772_, p_46773_, p_46774_);
   }

   public void m_214171_(GameEvent pEvent, Vec3 pPosition, GameEvent.Context pContext) {
      this.world.m_214171_(pEvent, pPosition, pContext);
   }

   public void m_142346_(@org.jetbrains.annotations.Nullable Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
      this.world.m_142346_(p_151549_, p_151550_, p_151551_);
   }

   public RegistryAccess m_5962_() {
      return this.world.m_5962_();
   }

   public float m_7717_(Direction p_45522_, boolean p_45523_) {
      return this.world.m_7717_(p_45522_, p_45523_);
   }

   public LevelLightEngine m_5518_() {
      return this.world.m_5518_();
   }

   public WorldBorder m_6857_() {
      return this.world.m_6857_();
   }

   public List<Entity> m_6249_(@org.jetbrains.annotations.Nullable Entity p_45936_, AABB p_45937_, Predicate<? super Entity> p_45938_) {
      return this.world.m_6249_(p_45936_, p_45937_, p_45938_);
   }

   public <T extends Entity> List<T> m_142425_(EntityTypeTest<Entity, T> p_151464_, AABB p_151465_, Predicate<? super T> p_151466_) {
      return this.world.m_142425_(p_151464_, p_151465_, p_151466_);
   }

   public List<? extends Player> m_6907_() {
      return this.world.m_6907_();
   }

   public @org.jetbrains.annotations.Nullable ChunkAccess m_6522_(int p_46823_, int p_46824_, ChunkStatus p_46825_, boolean p_46826_) {
      return this.world.m_6522_(p_46823_, p_46824_, p_46825_, p_46826_);
   }

   public int m_6924_(Heightmap.Types p_46827_, int p_46828_, int p_46829_) {
      return this.world.m_6924_(p_46827_, p_46828_, p_46829_);
   }

   public int m_7445_() {
      return this.world.m_7445_();
   }

   public BiomeManager m_7062_() {
      return this.world.m_7062_();
   }

   public Holder<Biome> m_203675_(int p_46809_, int p_46810_, int p_46811_) {
      return this.world.m_203675_(p_46809_, p_46810_, p_46811_);
   }

   public boolean m_5776_() {
      return this.world.m_5776_();
   }

   public int m_5736_() {
      return this.world.m_5736_();
   }

   public DimensionType m_6042_() {
      return this.world.m_6042_();
   }

   public boolean m_7433_(BlockPos p_46938_, Predicate<BlockState> p_46939_) {
      return this.world.m_7433_(p_46938_, p_46939_);
   }

   public boolean m_142433_(BlockPos p_151584_, Predicate<FluidState> p_151585_) {
      return this.world.m_142433_(p_151584_, p_151585_);
   }
}
