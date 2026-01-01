package com.yymod.mechanicalrepairstation.registries;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.simibubi.create.api.stress.BlockStressValues;
import com.yymod.mechanicalrepairstation.config.MWConfigs;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.mechanicalrepairstationBlock;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.mechanicalrepairstationGenerator;
import net.minecraft.world.level.block.Blocks;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.yymod.mechanicalrepairstation.YYmechanicalrepairstation.REGISTRATE;

public class MWBlocks {

    public static final BlockEntry<mechanicalrepairstationBlock> mechanical_repair_station = REGISTRATE
            .block("mechanical_repair_station", mechanicalrepairstationBlock::new)
            .initialProperties(() -> Blocks.CRAFTING_TABLE)
            .transform(axeOnly())
            .blockstate(new mechanicalrepairstationGenerator()::generate)
            .item()
            .transform(customItemModel("mechanical_repair_station", "item"))
            .register();

    public static void register() {
        BlockStressValues.IMPACTS.register(mechanical_repair_station.get(),
                () -> MWConfigs.common().mechanicalrepairstation.stressImpact.get());
    }
}

