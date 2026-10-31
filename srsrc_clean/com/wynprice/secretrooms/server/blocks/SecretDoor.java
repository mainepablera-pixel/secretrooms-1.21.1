package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.client.world.DummyIWorld;
import com.wynprice.secretrooms.server.blocks.states.SecretMappedModelState;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

public class SecretDoor extends SecretBaseBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty OPEN;
   public static final EnumProperty<DoorHingeSide> HINGE;
   public static final BooleanProperty POWERED;
   public static final EnumProperty<DoubleBlockHalf> HALF;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape EAST_AABB;

   public SecretDoor(BlockBehaviour.Properties properties) {
      super(properties);
      this.m_49959_((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.m_49966_().m_61124_(FACING, Direction.NORTH)).m_61124_(OPEN, false)).m_61124_(HINGE, DoorHingeSide.LEFT)).m_61124_(POWERED, false)).m_61124_(HALF, DoubleBlockHalf.LOWER));
   }

   protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
      return new SecretMappedModelState(block, propertiesToValueMap, codec);
   }

   public void applyExtraModelData(BlockGetter world, BlockPos pos, BlockState state, ModelData.Builder builder) {
      builder.with(SecretModelData.MODEL_MAP_STATE, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)Blocks.f_50154_.m_49966_().m_61124_(FACING, (Direction)state.m_61143_(FACING))).m_61124_(OPEN, (Boolean)state.m_61143_(OPEN))).m_61124_(HINGE, (DoorHingeSide)state.m_61143_(HINGE))).m_61124_(POWERED, (Boolean)state.m_61143_(POWERED))).m_61124_(HALF, (DoubleBlockHalf)state.m_61143_(HALF)));
      super.applyExtraModelData(world, pos, state, builder);
   }

   public Boolean getSolidValue() {
      return false;
   }

   public boolean m_7923_(BlockState state) {
      return true;
   }

   public int m_7753_(BlockState state, BlockGetter world, BlockPos pos) {
      return 0;
   }

   public boolean m_7420_(BlockState state, BlockGetter world, BlockPos pos) {
      return false;
   }

   public VoxelShape m_5939_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.m_60808_(worldIn, pos);
   }

   public VoxelShape m_7952_(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return state.m_60808_(worldIn, pos);
   }

   public VoxelShape m_6079_(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return state.m_60808_(worldIn, pos);
   }

   public VoxelShape m_5909_(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return state.m_60808_(reader, pos);
   }

   public VoxelShape m_5940_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      Direction direction = (Direction)state.m_61143_(FACING);
      boolean flag = !(Boolean)state.m_61143_(OPEN);
      boolean flag1 = state.m_61143_(HINGE) == DoorHingeSide.RIGHT;
      VoxelShape mirrorShape = super.m_5940_(state, worldIn, pos, context);
      VoxelShape shape;
      switch (direction) {
         case EAST:
         default:
            shape = flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
            break;
         case SOUTH:
            shape = flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
            break;
         case WEST:
            shape = flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
            break;
         case NORTH:
            shape = flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
      }

      return Shapes.m_83113_(shape, mirrorShape, BooleanOp.f_82689_);
   }

   public BlockState m_7417_(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)stateIn.m_61143_(HALF);
      if (facing.m_122434_() == Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
         return facingState.m_60734_() == this && facingState.m_61143_(HALF) != doubleblockhalf ? (BlockState)((BlockState)((BlockState)((BlockState)stateIn.m_61124_(FACING, (Direction)facingState.m_61143_(FACING))).m_61124_(OPEN, (Boolean)facingState.m_61143_(OPEN))).m_61124_(HINGE, (DoorHingeSide)facingState.m_61143_(HINGE))).m_61124_(POWERED, (Boolean)facingState.m_61143_(POWERED)) : Blocks.f_50016_.m_49966_();
      } else {
         return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.m_60710_(worldIn, currentPos) ? Blocks.f_50016_.m_49966_() : stateIn;
      }
   }

   public void m_5707_(Level worldIn, BlockPos pos, BlockState state, Player player) {
      if (!worldIn.f_46443_ && player.m_7500_()) {
         DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)state.m_61143_(HALF);
         BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.m_7494_() : pos.m_7495_();
         BlockState blockstate = worldIn.m_8055_(blockpos);
         Optional<BlockState> mirror = getMirrorState(worldIn, pos);
         if (blockstate.m_60734_() == this && blockstate.m_61143_(HALF) != doubleblockhalf) {
            worldIn.m_7731_(blockpos, Blocks.f_50016_.m_49966_(), 35);
            mirror.ifPresent((blockState) -> worldIn.m_5898_(player, 2001, blockpos, Block.m_49956_(blockState)));
         }
      }

      super.m_5707_(worldIn, pos, state, player);
   }

   public boolean m_7357_(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
      switch (type) {
         case LAND:
         case AIR:
            return (Boolean)state.m_61143_(OPEN);
         default:
            return false;
      }
   }

   private int getCloseSound() {
      return this.f_60442_ == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1011 : 1012;
   }

   private int getOpenSound() {
      return this.f_60442_ == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1005 : 1006;
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext context) {
      BlockPos blockpos = context.m_8083_();
      if (blockpos.m_123342_() < 255 && context.m_43725_().m_8055_(blockpos.m_7494_()).m_60629_(context)) {
         Level world = context.m_43725_();
         boolean flag = world.m_46753_(blockpos) || world.m_46753_(blockpos.m_7494_());
         return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.m_49966_().m_61124_(FACING, context.m_8125_())).m_61124_(HINGE, this.getHingeSide(context))).m_61124_(POWERED, flag)).m_61124_(OPEN, flag)).m_61124_(HALF, DoubleBlockHalf.LOWER);
      } else {
         return null;
      }
   }

   public void m_6402_(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      worldIn.m_7731_(pos.m_7494_(), (BlockState)state.m_61124_(HALF, DoubleBlockHalf.UPPER), 3);
   }

   private DoorHingeSide getHingeSide(BlockPlaceContext p_208073_1_) {
      BlockGetter iblockreader = p_208073_1_.m_43725_();
      BlockPos blockpos = p_208073_1_.m_8083_();
      Direction direction = p_208073_1_.m_8125_();
      BlockPos blockpos1 = blockpos.m_7494_();
      Direction direction1 = direction.m_122428_();
      BlockPos blockpos2 = blockpos.m_121945_(direction1);
      BlockState blockstate = iblockreader.m_8055_(blockpos2);
      BlockPos blockpos3 = blockpos1.m_121945_(direction1);
      BlockState blockstate1 = iblockreader.m_8055_(blockpos3);
      Direction direction2 = direction.m_122427_();
      BlockPos blockpos4 = blockpos.m_121945_(direction2);
      BlockState blockstate2 = iblockreader.m_8055_(blockpos4);
      BlockPos blockpos5 = blockpos1.m_121945_(direction2);
      BlockState blockstate3 = iblockreader.m_8055_(blockpos5);
      int i = (blockstate.m_60835_(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.m_60835_(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.m_60835_(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.m_60835_(iblockreader, blockpos5) ? 1 : 0);
      boolean flag = blockstate.m_60734_() == this && blockstate.m_61143_(HALF) == DoubleBlockHalf.LOWER;
      boolean flag1 = blockstate2.m_60734_() == this && blockstate2.m_61143_(HALF) == DoubleBlockHalf.LOWER;
      if ((!flag || flag1) && i <= 0) {
         if ((!flag1 || flag) && i >= 0) {
            int j = direction.m_122429_();
            int k = direction.m_122431_();
            Vec3 vec3d = p_208073_1_.m_43720_();
            double d0 = vec3d.f_82479_ - (double)blockpos.m_123341_();
            double d1 = vec3d.f_82481_ - (double)blockpos.m_123343_();
            return j < 0 && d1 < (double)0.5F || j > 0 && d1 > (double)0.5F || k < 0 && d0 > (double)0.5F || k > 0 && d0 < (double)0.5F ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (this.f_60442_ == SecretBlocks.Materials.SRM_MATERIAL_IRON) {
         return InteractionResult.FAIL;
      } else {
         BlockPos other = state.m_61143_(HALF) == DoubleBlockHalf.LOWER ? pos.m_7494_() : pos.m_7495_();
         boolean rotateClockwise = state.m_61143_(HINGE) == DoorHingeSide.LEFT == (Boolean)state.m_61143_(OPEN);
         Rotation rotation = rotateClockwise ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
         LevelAccessor world = new DummyIWorld(worldIn);
         getMirrorData(worldIn, pos).ifPresent((d) -> d.setBlockState(d.getBlockState().rotate(world, pos, rotation)));
         getMirrorData(worldIn, other).ifPresent((d) -> d.setBlockState(d.getBlockState().rotate(world, other, rotation)));
         state = (BlockState)state.m_61122_(OPEN);
         worldIn.m_7731_(pos, state, 10);
         worldIn.m_5898_(player, (Boolean)state.m_61143_(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
         requestModelRefresh(worldIn, pos);
         requestModelRefresh(worldIn, other);
         return InteractionResult.SUCCESS;
      }
   }

   public void m_6861_(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      boolean flag = worldIn.m_46753_(pos) || worldIn.m_46753_(pos.m_121945_(state.m_61143_(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
      if (blockIn != this && flag != (Boolean)state.m_61143_(POWERED)) {
         if (flag != (Boolean)state.m_61143_(OPEN)) {
            this.playSound(worldIn, pos, flag);
            requestModelRefresh(worldIn, pos);
            requestModelRefresh(worldIn, state.m_61143_(HALF) == DoubleBlockHalf.LOWER ? pos.m_7494_() : pos.m_7495_());
         }

         worldIn.m_7731_(pos, (BlockState)((BlockState)state.m_61124_(POWERED, flag)).m_61124_(OPEN, flag), 2);
      }

   }

   public boolean m_7898_(BlockState state, LevelReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.m_7495_();
      BlockState blockstate = worldIn.m_8055_(blockpos);
      if (state.m_61143_(HALF) == DoubleBlockHalf.LOWER) {
         return blockstate.m_60783_(worldIn, blockpos, Direction.UP);
      } else {
         return blockstate.m_60734_() == this;
      }
   }

   private void playSound(Level p_196426_1_, BlockPos p_196426_2_, boolean p_196426_3_) {
      p_196426_1_.m_5898_((Player)null, p_196426_3_ ? this.getOpenSound() : this.getCloseSound(), p_196426_2_, 0);
   }

   public PushReaction m_5537_(BlockState state) {
      return PushReaction.DESTROY;
   }

   public BlockState m_6843_(BlockState state, Rotation rot) {
      return (BlockState)state.m_61124_(FACING, rot.m_55954_((Direction)state.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState state, Mirror mirrorIn) {
      return mirrorIn == Mirror.NONE ? state : state.m_60717_(mirrorIn.m_54846_((Direction)state.m_61143_(FACING)));
   }

   @OnlyIn(Dist.CLIENT)
   public long m_7799_(BlockState state, BlockPos pos) {
      return Mth.m_14130_(pos.m_123341_(), pos.m_6625_(state.m_61143_(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).m_123342_(), pos.m_123343_());
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{HALF, FACING, OPEN, HINGE, POWERED});
   }

   static {
      FACING = HorizontalDirectionalBlock.f_54117_;
      OPEN = BlockStateProperties.f_61446_;
      HINGE = BlockStateProperties.f_61394_;
      POWERED = BlockStateProperties.f_61448_;
      HALF = BlockStateProperties.f_61401_;
      SOUTH_AABB = Block.m_49796_((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)3.0F);
      NORTH_AABB = Block.m_49796_((double)0.0F, (double)0.0F, (double)13.0F, (double)16.0F, (double)16.0F, (double)16.0F);
      WEST_AABB = Block.m_49796_((double)13.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)16.0F, (double)16.0F);
      EAST_AABB = Block.m_49796_((double)0.0F, (double)0.0F, (double)0.0F, (double)3.0F, (double)16.0F, (double)16.0F);
   }
}
