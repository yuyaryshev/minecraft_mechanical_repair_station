package com.yymod.mechanicalworkbench.registries;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench.MechanicalWorkbenchBlock;
import com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench.MechanicalWorkbenchGenerator;
import net.minecraft.world.level.block.Blocks;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.yymod.mechanicalworkbench.YYMechanicalWorkbench.REGISTRATE;

public class MWBlocks {

    public static final BlockEntry<MechanicalWorkbenchBlock> MECHANICAL_WORKBENCH = REGISTRATE
            .block("mechanical_workbench", MechanicalWorkbenchBlock::new)
            .initialProperties(() -> Blocks.CRAFTING_TABLE)
            .transform(axeOnly())
            .blockstate(new MechanicalWorkbenchGenerator()::generate)
            .item()
            .transform(customItemModel("mechanical_workbench", "item"))
            .register();

    public static void register() {
    }
}
