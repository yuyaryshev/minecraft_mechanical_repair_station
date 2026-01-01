package com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.yymod.mechanicalworkbench.registries.MWPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MechanicalWorkbenchRenderer extends SafeBlockEntityRenderer<MechanicalWorkbenchBlockEntity> {

    public MechanicalWorkbenchRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(MechanicalWorkbenchBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        renderAxis(be, ms, buffer, light);
    }

    private void renderAxis(MechanicalWorkbenchBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
        BlockState blockState = be.getBlockState();
        SuperByteBuffer axis = CachedBuffers.partialFacing(MWPartialModels.MECHANICAL_WORKBENCH_AXIS, blockState,
                blockState.getValue(HORIZONTAL_FACING));
        axis.color(0xFFFFFFFF);
        KineticBlockEntityRenderer.renderRotatingBuffer(be, axis, ms, buffer.getBuffer(RenderType.cutoutMipped()),
                LightTexture.FULL_BRIGHT);
    }
}


