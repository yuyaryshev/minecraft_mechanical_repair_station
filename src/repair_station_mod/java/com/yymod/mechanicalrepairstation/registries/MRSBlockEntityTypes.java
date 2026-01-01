package com.yymod.mechanicalrepairstation.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationBlockEntity;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.MechanicalRepairStationRenderer;

import static com.yymod.mechanicalrepairstation.YYMechanicalRepairStation.REGISTRATE;

public class MRSBlockEntityTypes {

    public static final BlockEntityEntry<MechanicalRepairStationBlockEntity> MECHANICAL_REPAIR_STATION = REGISTRATE
            .blockEntity("mechanical_repair_station", MechanicalRepairStationBlockEntity::new)
            .validBlocks(MRSBlocks.MECHANICAL_REPAIR_STATION)
            .renderer(() -> MechanicalRepairStationRenderer::new)
            .register();

    public static void register() {
    }
}

