package com.yymod.mechanicalrepairstation.registries;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationBlock;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationGenerator;
import net.minecraft.world.level.block.Blocks;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.yymod.mechanicalrepairstation.YYMechanicalRepairStation.REGISTRATE;

public class MRSBlocks {

    public static final BlockEntry<MechanicalRepairStationBlock> MECHANICAL_REPAIR_STATION = REGISTRATE
            .block("mechanical_repair_station", MechanicalRepairStationBlock::new)
            .initialProperties(() -> Blocks.CRAFTING_TABLE)
            .blockstate(new MechanicalRepairStationGenerator()::generate)
            .item()
            .transform(customItemModel("mechanical_repair_station", "item"))
            .register();

    public static void register() {
    }
}

