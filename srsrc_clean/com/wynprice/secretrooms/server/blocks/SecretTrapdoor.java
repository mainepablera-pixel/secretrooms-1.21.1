package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.blocks.states.SecretMappedModelState;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;

public class SecretTrapdoor extends SecretBaseBlock {
   private static final BooleanProperty OPEN;
   private static final EnumProperty<Half> HALF;
   private static final DirectionProperty HORIZONTAL_FACING;
   private static final BooleanProperty POWERED;
   private static final VoxelShape EAST_OPEN_AABB;
   private static final VoxelShape WEST_OPEN_AABB;
   private static final VoxelShape SOUTH_OPEN_AABB;
   private static final VoxelShape NORTH_OPEN_AABB;
   private static final VoxelShape BOTTOM_AABB;
   private static final VoxelShape TOP_AABB;

   public SecretTrapdoor(BlockBehaviour.Properties properties) {
      super(properties);
      this.m_49959_((BlockState)((BlockState)((BlockState)((BlockState)this.m_49966_().m_61124_(HORIZONTAL_FACING, Direction.NORTH)).m_61124_(OPEN, false)).m_61124_(HALF, Half.BOTTOM)).m_61124_(POWERED, false));
   }

   protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
      return new SecretMappedModelState(block, propertiesToValueMap, codec);
   }

   public void applyExtraModelData(BlockGetter world, BlockPos pos, BlockState state, ModelData.Builder builder) {
      builder.with(SecretModelData.MODEL_MAP_STATE, (BlockState)((BlockState)((BlockState)Blocks.f_50216_.m_49966_().m_61124_(HORIZONTAL_FACING, (Direction)state.m_61143_(HORIZONTAL_FACING))).m_61124_(OPEN, (Boolean)state.m_61143_(OPEN))).m_61124_(HALF, (Half)state.m_61143_(HALF)));
   }

   public VoxelShape m_5940_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      VoxelShape mirrorShape = super.m_5940_(state, worldIn, pos, context);
      VoxelShape shape;
      if (!(Boolean)state.m_61143_(OPEN)) {
         shape = state.m_61143_(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch ((Direction)state.m_61143_(HORIZONTAL_FACING)) {
            case NORTH:
            default:
               shape = NORTH_OPEN_AABB;
               break;
            case SOUTH:
               shape = SOUTH_OPEN_AABB;
               break;
            case WEST:
               shape = WEST_OPEN_AABB;
               break;
            case EAST:
               shape = EAST_OPEN_AABB;
         }
      }

      return Shapes.m_83113_(shape, mirrorShape, BooleanOp.f_82689_);
   }

   public VoxelShape m_5939_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return this.m_5940_(state, worldIn, pos, context);
   }

   public VoxelShape m_6079_(BlockState state, BlockGetter world, BlockPos pos) {
      return this.m_5940_(state, world, pos, CollisionContext.m_82749_());
   }

   public VoxelShape m_5909_(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return state.m_60808_(reader, pos);
   }

   public VoxelShape m_7952_(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return this.m_5940_(state, worldIn, pos, CollisionContext.m_82749_());
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

   public boolean m_7357_(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
      switch (type) {
         case LAND:
         case AIR:
            return (Boolean)state.m_61143_(OPEN);
         default:
            return false;
      }
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (this.f_60442_ == SecretBlocks.Materials.SRM_MATERIAL_IRON) {
         return InteractionResult.PASS;
      } else {
         state = (BlockState)state.m_61122_(OPEN);
         worldIn.m_7731_(pos, state, 2);
         requestModelRefresh(worldIn, pos);
         this.playSound(player, worldIn, pos, (Boolean)state.m_61143_(OPEN));
         return InteractionResult.SUCCESS;
      }
   }

   protected void playSound(@Nullable Player player, Level worldIn, BlockPos pos, boolean p_185731_4_) {
      if (p_185731_4_) {
         int i = this.f_60442_ == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1037 : 1007;
         worldIn.m_5898_(player, i, pos, 0);
      } else {
         int j = this.f_60442_ == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1036 : 1013;
         worldIn.m_5898_(player, j, pos, 0);
      }

   }

   public void m_6861_(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.f_46443_) {
         boolean flag = worldIn.m_46753_(pos);
         if (flag != (Boolean)state.m_61143_(POWERED)) {
            if ((Boolean)state.m_61143_(OPEN) != flag) {
               state = (BlockState)state.m_61124_(OPEN, flag);
               this.playSound((Player)null, worldIn, pos, flag);
            }

            worldIn.m_7731_(pos, (BlockState)state.m_61124_(POWERED, flag), 2);
            requestModelRefresh(worldIn, pos);
            if ((Boolean)state.m_61143_(BlockStateProperties.f_61362_)) {
               worldIn.m_186469_(pos, Fluids.f_76193_, Fluids.f_76193_.m_6718_(worldIn));
            }
         }
      }

   }

   public BlockState m_7417_(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return stateIn;
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      BlockState blockstate = this.m_49966_();
      Direction direction = context.m_43719_();
      if (!context.m_7058_() && direction.m_122434_().m_122479_()) {
         blockstate = (BlockState)((BlockState)blockstate.m_61124_(HORIZONTAL_FACING, direction)).m_61124_(HALF, context.m_43720_().f_82480_ - (double)context.m_8083_().m_123342_() > (double)0.5F ? Half.TOP : Half.BOTTOM);
      } else {
         blockstate = (BlockState)((BlockState)blockstate.m_61124_(HORIZONTAL_FACING, context.m_8125_().m_122424_())).m_61124_(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
      }

      if (context.m_43725_().m_46753_(context.m_8083_())) {
         blockstate = (BlockState)((BlockState)blockstate.m_61124_(OPEN, Boolean.TRUE)).m_61124_(POWERED, Boolean.TRUE);
      }

      return blockstate;
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{HORIZONTAL_FACING, OPEN, HALF, POWERED});
   }

   public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
      if ((Boolean)state.m_61143_(OPEN)) {
         BlockState down = world.m_8055_(pos.m_7495_());
         if (down.m_60734_() == Blocks.f_50155_) {
            return down.m_61143_(LadderBlock.f_54337_) == state.m_61143_(HORIZONTAL_FACING);
         }
      }

      return false;
   }

   public boolean canEntitySpawn(BlockState state, BlockGetter worldIn, BlockPos pos, EntityType<?> type) {
      return false;
   }

   static {
      OPEN = BlockStateProperties.f_61446_;
      HALF = BlockStateProperties.f_61402_;
      HORIZONTAL_FACING = BlockStateProperties.f_61374_;
      POWERED = BlockStateProperties.f_61448_;
      EAST_OPEN_AABB = Block.m_49796_(0.00101, 0.001, 0.001, (double)3.0F, 15.999, 15.999);
      WEST_OPEN_AABB = Block.m_49796_((double)13.0F, 0.001, 0.001, 15.999, 15.999, 15.999);
      SOUTH_OPEN_AABB = Block.m_49796_(0.001, 0.001, 0.001, 15.999, 15.999, (double)3.0F);
      NORTH_OPEN_AABB = Block.m_49796_(0.001, 0.001, (double)13.0F, 15.999, 15.999, 15.999);
      BOTTOM_AABB = Block.m_49796_(0.001, 0.001, 0.001, 15.999, (double)3.0F, 15.999);
      TOP_AABB = Block.m_49796_(0.001, (double)13.0F, 0.001, 15.999, 15.999, 15.999);
   }
}
