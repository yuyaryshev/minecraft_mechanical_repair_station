package com.yymod.mechanicalrepairstation.registries;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.yymod.mechanicalrepairstation.YYmechanicalrepairstation;

public class MWPartialModels {

    public static final PartialModel mechanical_repair_station_AXIS = block("mechanical_repair_station/axis");

    private static PartialModel block(String path) {
        return PartialModel.of(YYmechanicalrepairstation.genRL("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}

