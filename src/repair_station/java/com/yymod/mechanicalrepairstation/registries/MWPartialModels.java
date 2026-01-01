package com.yymod.mechanicalworkbench.registries;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.yymod.mechanicalworkbench.YYMechanicalWorkbench;

public class MWPartialModels {

    public static final PartialModel MECHANICAL_WORKBENCH_AXIS = block("mechanical_workbench/axis");

    private static PartialModel block(String path) {
        return PartialModel.of(YYMechanicalWorkbench.genRL("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
