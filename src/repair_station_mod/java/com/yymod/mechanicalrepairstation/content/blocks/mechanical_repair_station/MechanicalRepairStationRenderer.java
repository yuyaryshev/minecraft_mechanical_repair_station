package com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.yymod.mechanicalrepairstation.registries.MRSPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MechanicalRepairStationRenderer extends SafeBlockEntityRenderer<MechanicalRepairStationBlockEntity> {

    public MechanicalRepairStationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(MechanicalRepairStationBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        renderAxis(be, ms, buffer, light);
    }

    private void renderAxis(MechanicalRepairStationBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
        BlockState blockState = be.getBlockState();
        SuperByteBuffer axis = CachedBuffers.partialFacing(MRSPartialModels.MECHANICAL_REPAIR_STATION_AXIS, blockState,
                blockState.getValue(HORIZONTAL_FACING));
        axis.color(0xFFFFFFFF);
        KineticBlockEntityRenderer.renderRotatingBuffer(be, axis, ms, buffer.getBuffer(RenderType.cutoutMipped()),
                LightTexture.FULL_BRIGHT);
    }
}



