package com.yymod.mechanicalrepairstation.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.mechanicalrepairstationBlockEntity;
import com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station.mechanicalrepairstationRenderer;

import static com.yymod.mechanicalrepairstation.YYmechanicalrepairstation.REGISTRATE;

public class MWBlockEntityTypes {

    public static final BlockEntityEntry<mechanicalrepairstationBlockEntity> mechanical_repair_station = REGISTRATE
            .blockEntity("mechanical_repair_station", mechanicalrepairstationBlockEntity::new)
            .validBlocks(MWBlocks.mechanical_repair_station)
            .renderer(() -> mechanicalrepairstationRenderer::new)
            .register();

    public static void register() {
    }
}

