package com.yymod.mechanicalrepairstation.registries;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.yymod.mechanicalrepairstation.YYMechanicalRepairStation;

public class MRSPartialModels {

    public static final PartialModel MECHANICAL_REPAIR_STATION_AXIS = block("mechanical_repair_station/axis");

    private static PartialModel block(String path) {
        return PartialModel.of(YYMechanicalRepairStation.genRL("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}

