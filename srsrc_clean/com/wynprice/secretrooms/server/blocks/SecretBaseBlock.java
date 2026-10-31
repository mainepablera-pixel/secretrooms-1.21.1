package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.client.world.DummyIWorld;
import com.wynprice.secretrooms.server.blocks.states.SecretBaseState;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.fml.DistExecutor;

public class SecretBaseBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty SOLID = BooleanProperty.m_61465_("solid");
   private final StateDefinition<Block, BlockState> stateContainerOverride;
   private int calls = 0;

   public SecretBaseBlock(BlockBehaviour.Properties properties) {
      super(properties.m_60988_());
      StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder(this);
      this.m_7926_(builder);
      this.stateContainerOverride = builder.m_61101_(Block::m_49966_, this::createNewState);
      this.m_49959_((BlockState)((BlockState)((BlockState)this.m_49965_().m_61090_()).m_61124_(SOLID, false)).m_61124_(BlockStateProperties.f_61362_, false));
   }

   public RenderShape m_7514_(BlockState p_49232_) {
      return RenderShape.MODEL;
   }

   protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
      return new SecretBaseState(block, propertiesToValueMap, codec);
   }

   public boolean m_7361_(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
      boolean value = (Boolean)getValue(worldIn, pos, (m, reader, p) -> m.m_60734_() instanceof SimpleWaterloggedBlock && ((SimpleWaterloggedBlock)m.m_60734_()).m_6044_(reader, pos, m, fluidStateIn.m_76152_()), () -> false);
      if (value) {
         boolean fluid = super.m_7361_(worldIn, pos, state, fluidStateIn);
         if (fluid) {
            getMirrorData(worldIn, pos).ifPresent((d) -> {
               BlockState mirror = d.getBlockState();
               if (mirror.m_61138_(BlockStateProperties.f_61362_)) {
                  d.setBlockState((BlockState)mirror.m_61124_(BlockStateProperties.f_61362_, true));
               }

            });
            return super.m_7361_(worldIn, pos, state, fluidStateIn);
         }
      }

      return false;
   }

   public ItemStack m_142598_(LevelAccessor worldIn, BlockPos pos, BlockState state) {
      ItemStack itemStack = super.m_142598_(worldIn, pos, state);
      if (!itemStack.m_41619_()) {
         getMirrorData(worldIn, pos).ifPresent((d) -> {
            BlockState mirror = d.getBlockState();
            if (mirror.m_61138_(BlockStateProperties.f_61362_)) {
               d.setBlockState((BlockState)mirror.m_61124_(BlockStateProperties.f_61362_, false));
            }

         });
      }

      return itemStack;
   }

   public StateDefinition<Block, BlockState> m_49965_() {
      return this.stateContainerOverride;
   }

   public FluidState m_5888_(BlockState state) {
      return (Boolean)state.m_61143_(BlockStateProperties.f_61362_) ? Fluids.f_76193_.m_76068_(false) : super.m_5888_(state);
   }

   public BlockState m_7417_(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      getMirrorData(worldIn, currentPos).ifPresent((data) -> {
         BlockState mirror = data.getBlockState();
         DummyIWorld world = new DummyIWorld(worldIn);
         if (!(facingState.m_60734_() instanceof SecretBaseBlock) && this.calls != 3) {
            ++this.calls;
            BlockState facingNewState = facingState.m_60728_(facing.m_122424_(), mirror, world, facingPos, currentPos);
            --this.calls;
            Block.m_49902_(facingState, facingNewState, worldIn, facingPos, 3);
         }

         BlockState newState = mirror.m_60728_(facing, world.m_8055_(facingPos), world, currentPos, facingPos);
         if (newState != mirror) {
            data.setBlockState(newState);
            BlockEntity tileEntity = world.m_7702_(currentPos);
            data.setTileEntityNBT(tileEntity != null ? tileEntity.serializeNBT() : null);
         }

      });
      return super.m_7417_(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void m_6402_(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      getMirrorState(worldIn, pos).ifPresent((mirror) -> mirror.m_60701_(new DummyIWorld(worldIn), pos, 3));
      super.m_6402_(worldIn, pos, state, placer, stack);
   }

   public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
      return (SoundType)getValue(world, pos, (mirror, reader, pos1) -> mirror.getSoundType(world, pos1, entity), () -> super.getSoundType(state, world, pos, entity));
   }

   public @org.jetbrains.annotations.Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @org.jetbrains.annotations.Nullable Mob mob) {
      return (BlockPathTypes)getValue(level, pos, (mirror, reader, pos1) -> mirror.getBlockPathType(reader, pos1, mob), () -> super.getBlockPathType(state, level, pos, mob));
   }

   public boolean m_7357_(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
      return (Boolean)getValue(world, pos, (mirror, reader, pos1) -> mirror.m_60647_(reader, pos, type), () -> super.m_7357_(state, world, pos, type));
   }

   public float m_7749_(BlockState state, BlockGetter world, BlockPos pos) {
      return (Float)getValue(world, pos, (mirror, reader, pos1) -> mirror.m_60792_(reader, pos), () -> super.m_7749_(state, world, pos));
   }

   public VoxelShape m_5940_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return (VoxelShape)getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.m_60651_(reader, pos1, context), () -> super.m_5940_(state, worldIn, pos, context));
   }

   public VoxelShape m_7947_(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return (VoxelShape)getValue(worldIn, pos, BlockBehaviour.BlockStateBase::m_60816_, () -> super.m_7947_(state, worldIn, pos));
   }

   public VoxelShape m_5939_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return (VoxelShape)getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.m_60742_(reader, pos1, context), () -> super.m_5939_(state, worldIn, pos, context));
   }

   public VoxelShape m_7952_(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return (VoxelShape)getValue(worldIn, pos, BlockBehaviour.BlockStateBase::m_60768_, () -> super.m_7952_(state, worldIn, pos));
   }

   public VoxelShape m_5909_(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return (VoxelShape)getValue(reader, pos, (m, w, p) -> m.m_60771_(w, p, context), () -> super.m_5909_(state, reader, pos, context));
   }

   public VoxelShape m_6079_(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return (VoxelShape)getValue(worldIn, pos, BlockBehaviour.BlockStateBase::m_60820_, () -> super.m_6079_(state, worldIn, pos));
   }

   public float getFriction(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
      return (Float)getValue(world, pos, (m, w, p) -> m.getFriction(world, p, entity), () -> super.getFriction(state, world, pos, entity));
   }

   public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
      int result = (Integer)getValue(world, pos, IForgeBlockState::getLightEmission, () -> super.getLightEmission(state, world, pos));
      StackTraceElement element = Thread.currentThread().getStackTrace()[3];
      if ("net.minecraft.client.renderer.block.ModelBlockRenderer".equals(element.getClassName())) {
         Optional<BlockState> mirrorState = getMirrorState(world, pos);
         if (mirrorState.isPresent()) {
            Boolean isAoModel = (Boolean)DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.m_91087_().m_91289_().m_110910_((BlockState)mirrorState.get()).m_7541_());
            if (isAoModel != null) {
               return result == 0 && isAoModel ? 0 : 1;
            }
         }
      }

      return result;
   }

   public int m_7753_(BlockState state, BlockGetter world, BlockPos pos) {
      return (Integer)getValue(world, pos, BlockBehaviour.BlockStateBase::m_60739_, () -> super.m_7753_(state, world, pos));
   }

   public boolean m_7420_(BlockState state, BlockGetter world, BlockPos pos) {
      return (Boolean)getValue(world, pos, BlockBehaviour.BlockStateBase::m_60631_, () -> super.m_7420_(state, world, pos));
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{SOLID, BlockStateProperties.f_61362_});
   }

   public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
      Optional<BlockState> mirrorState = getMirrorState(world, pos);
      if (mirrorState.isPresent()) {
         BlockState blockstate = (BlockState)mirrorState.get();
         if (blockstate.m_60799_() != RenderShape.INVISIBLE) {
            Vec3 vec3d = entity.m_20184_();
            world.m_7106_(new BlockParticleOption(ParticleTypes.f_123794_, blockstate), entity.m_20185_() + ((double)world.f_46441_.m_188501_() - (double)0.5F) * (double)entity.m_20205_(), entity.m_20186_() + 0.1, entity.m_20189_() + ((double)world.f_46441_.m_188501_() - (double)0.5F) * (double)entity.m_20205_(), vec3d.f_82479_ * (double)-4.0F, (double)1.5F, vec3d.f_82481_ * (double)-4.0F);
         }
      }

      return true;
   }

   public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
      consumer.accept(new IClientBlockExtensions() {
         public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
            if (target instanceof BlockHitResult) {
               BlockPos pos = ((BlockHitResult)target).m_82425_();
               Optional<BlockState> mirrorState = SecretBaseBlock.getMirrorState(level, pos);
               if (mirrorState.isPresent()) {
                  BlockState blockstate = (BlockState)mirrorState.get();
                  Direction side = ((BlockHitResult)target).m_82434_();
                  if (blockstate.m_60799_() != RenderShape.INVISIBLE) {
                     int x = pos.m_123341_();
                     int y = pos.m_123342_();
                     int z = pos.m_123343_();
                     AABB axisalignedbb = blockstate.m_60808_(level, pos).m_83215_();
                     double xPos = (double)x + level.f_46441_.m_188500_() * (axisalignedbb.f_82291_ - axisalignedbb.f_82288_ - (double)0.2F) + (double)0.1F + axisalignedbb.f_82288_;
                     double yPos = (double)y + level.f_46441_.m_188500_() * (axisalignedbb.f_82292_ - axisalignedbb.f_82289_ - (double)0.2F) + (double)0.1F + axisalignedbb.f_82289_;
                     double zPos = (double)z + level.f_46441_.m_188500_() * (axisalignedbb.f_82293_ - axisalignedbb.f_82290_ - (double)0.2F) + (double)0.1F + axisalignedbb.f_82290_;
                     switch (side) {
                        case UP -> yPos = (double)y + axisalignedbb.f_82292_ + (double)0.1F;
                        case DOWN -> yPos = (double)y + axisalignedbb.f_82289_ - (double)0.1F;
                        case NORTH -> zPos = (double)z + axisalignedbb.f_82290_ - (double)0.1F;
                        case SOUTH -> zPos = (double)z + axisalignedbb.f_82293_ + (double)0.1F;
                        case WEST -> xPos = (double)x + axisalignedbb.f_82288_ - (double)0.1F;
                        case EAST -> xPos = (double)x + axisalignedbb.f_82291_ + (double)0.1F;
                     }

                     Minecraft.m_91087_().f_91061_.m_107344_((new TerrainParticle((ClientLevel)level, xPos, yPos, zPos, (double)0.0F, (double)0.0F, (double)0.0F, blockstate, pos)).m_107268_(0.2F).m_6569_(0.6F));
                  }
               }
            }

            return true;
         }

         public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
            Optional<BlockState> mirrorState = SecretBaseBlock.getMirrorState(level, pos);
            if (mirrorState.isPresent()) {
               if (((BlockState)mirrorState.get()).m_60795_()) {
                  return false;
               }

               BlockState blockstate = (BlockState)mirrorState.get();
               VoxelShape voxelshape = blockstate.m_60808_(level, pos);
               voxelshape.m_83286_((x1, y1, z1, x2, y2, z2) -> {
                  double xDelta = Math.min((double)1.0F, x2 - x1);
                  double yDelta = Math.min((double)1.0F, y2 - y1);
                  double zDelta = Math.min((double)1.0F, z2 - z1);
                  int xAmount = Math.max(2, Mth.m_14165_(xDelta / (double)0.25F));
                  int yAmount = Math.max(2, Mth.m_14165_(yDelta / (double)0.25F));
                  int zAmount = Math.max(2, Mth.m_14165_(zDelta / (double)0.25F));

                  for(int x = 0; x < xAmount; ++x) {
                     for(int y = 0; y < yAmount; ++y) {
                        for(int z = 0; z < zAmount; ++z) {
                           double dx = ((double)x + (double)0.5F) / (double)xAmount;
                           double dy = ((double)y + (double)0.5F) / (double)yAmount;
                           double dz = ((double)z + (double)0.5F) / (double)zAmount;
                           double xPos = dx * xDelta + x1;
                           double yPos = dy * yDelta + y1;
                           double zPos = dz * zDelta + z1;
                           Minecraft.m_91087_().f_91061_.m_107344_(new TerrainParticle((ClientLevel)level, (double)pos.m_123341_() + xPos, (double)pos.m_123342_() + yPos, (double)pos.m_123343_() + zPos, dx - (double)0.5F, dy - (double)0.5F, dz - (double)0.5F, blockstate, pos));
                        }
                     }
                  }

               });
            }

            return true;
         }
      });
   }

   public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
      getMirrorState(worldserver, pos).ifPresent((blockState) -> worldserver.m_8767_(new BlockParticleOption(ParticleTypes.f_123794_, blockState), entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), numberOfParticles, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.15F));
      return true;
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new SecretTileEntity(pos, state);
   }

   public void applyExtraModelData(BlockGetter world, BlockPos pos, BlockState state, ModelData.Builder builder) {
   }

   public Boolean getSolidValue() {
      return null;
   }

   public BlockState getPlaceState(BlockGetter wold, BlockPos placedOnPos, BlockState placedOn, BlockState fallback) {
      boolean waterlogged = placedOn.m_61138_(BlockStateProperties.f_61362_) && (Boolean)placedOn.m_61143_(BlockStateProperties.f_61362_);
      return (BlockState)((BlockState)fallback.m_61124_(SOLID, placedOn.m_60815_())).m_61124_(BlockStateProperties.f_61362_, waterlogged);
   }

   public static <T> T getValue(BlockGetter world, BlockPos pos, StateFunction<T> function, Supplier<T> defaultValue) {
      return (T)getMirrorState(world, pos).map(DelegateWorld.createFunction(world, (reader, mirror) -> function.getValue(mirror, reader, pos))).orElseGet(defaultValue);
   }

   public static Optional<BlockState> getMirrorState(BlockGetter world, BlockPos pos) {
      return getMirrorData(world, pos).map(SecretData::getBlockState);
   }

   public static Optional<SecretData> getMirrorData(BlockGetter world, BlockPos pos) {
      if (world != null && pos != null) {
         BlockState blockState = null;
         if (world instanceof Level) {
            LevelChunk chunk = ((Level)world).m_7726_().m_7131_(pos.m_123341_() >> 4, pos.m_123343_() >> 4);
            if (chunk != null) {
               blockState = chunk.m_8055_(pos);
            }
         } else {
            blockState = world.m_8055_(pos);
         }

         BlockEntity te = world.m_7702_(pos);
         return blockState != null && blockState.m_60734_() instanceof SecretBaseBlock && te instanceof SecretTileEntity ? Optional.of(((SecretTileEntity)te).getData()) : Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public static void requestModelRefresh(BlockGetter world, BlockPos pos) {
      BlockEntity tileEntity = world.m_7702_(pos);
      if (tileEntity != null) {
         tileEntity.requestModelDataUpdate();
      }

   }

   public interface StateFunction<T> {
      T getValue(BlockState var1, BlockGetter var2, BlockPos var3);
   }
}
