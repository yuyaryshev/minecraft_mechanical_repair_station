package com.yymod.mechanicalworkbench.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench.MechanicalWorkbenchBlockEntity;
import com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench.MechanicalWorkbenchRenderer;

import static com.yymod.mechanicalworkbench.YYMechanicalWorkbench.REGISTRATE;

public class MWBlockEntityTypes {

    public static final BlockEntityEntry<MechanicalWorkbenchBlockEntity> MECHANICAL_WORKBENCH = REGISTRATE
            .blockEntity("mechanical_workbench", MechanicalWorkbenchBlockEntity::new)
            .validBlocks(MWBlocks.MECHANICAL_WORKBENCH)
            .renderer(() -> MechanicalWorkbenchRenderer::new)
            .register();

    public static void register() {
    }
}
