package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import com.wynprice.secretrooms.server.tileentity.SecretChestTileEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for all camouflage / "secret" blocks.
 * Stores a mirrored BlockState in its BlockEntity and delegates many queries to it.
 */
public class SecretBaseBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty SOLID = BooleanProperty.create("solid");

    public SecretBaseBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SecretBaseBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SOLID, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SecretTileEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // SecretBlockItem / SecretDoubleBlockItem establish the camouflage from
        // the face used for placement. Do not overwrite that state here: doing
        // so made side-placed blocks copy the block below instead, and could
        // replace a valid inherited state with stone when stacking.
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    public static Optional<SecretTileEntity> getTile(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof SecretTileEntity ste ? Optional.of(ste) : Optional.empty();
    }

    public static Optional<BlockState> getMirrorState(BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SecretTileEntity secret) {
            return Optional.of(secret.getMirrorState());
        }
        if (blockEntity instanceof SecretChestTileEntity chest) {
            return Optional.of(chest.getMirrorState());
        }
        return Optional.empty();
    }

    /** Helper used by many delegated methods */
    protected static <T> T getValue(BlockGetter level, BlockPos pos,
                                    Function<BlockState, T> fromMirror,
                                    java.util.function.Supplier<T> fallback) {
        return getMirrorState(level, pos).map(fromMirror).orElseGet(fallback);
    }

    /** Use the camouflage block's material sounds for steps, hits, breaks and placement. */
    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return getMirrorState(level, pos)
                .filter(mirror -> !(mirror.getBlock() instanceof SecretBaseBlock))
                .map(mirror -> mirror.getSoundType(level, pos, entity))
                .orElseGet(() -> super.getSoundType(state, level, pos, entity));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getValue(level, pos, s -> s.getShape(level, pos, ctx), () -> super.getShape(state, level, pos, ctx));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getValue(level, pos, s -> s.getCollisionShape(level, pos, ctx), () -> super.getCollisionShape(state, level, pos, ctx));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockState state,
                               FluidState fluidState) {
        return SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable net.minecraft.world.entity.player.Player player, BlockGetter level,
                                  BlockPos pos, BlockState state, net.minecraft.world.level.material.Fluid fluid) {
        return SimpleWaterloggedBlock.super.canPlaceLiquid(player, level, pos, state, fluid);
    }
}
