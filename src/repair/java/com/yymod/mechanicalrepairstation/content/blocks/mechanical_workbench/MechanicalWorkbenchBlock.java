package com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.yymod.mechanicalworkbench.registries.MWBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class MechanicalWorkbenchBlock extends HorizontalKineticBlock implements IBE<MechanicalWorkbenchBlockEntity> {

    public MechanicalWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<MechanicalWorkbenchBlockEntity> getBlockEntityClass() {
        return MechanicalWorkbenchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalWorkbenchBlockEntity> getBlockEntityType() {
        return MWBlockEntityTypes.MECHANICAL_WORKBENCH.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING).getOpposite();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        return onBlockEntityUse(level, pos, be -> {
            NetworkHooks.openScreen((ServerPlayer) player, be, pos);
            return InteractionResult.CONSUME;
        });
    }

}


