package com.yymod.mechanicalrepairstation.config;

import net.createmod.catnip.config.ConfigBase;

public class MRSCommonConfig extends ConfigBase {

    public final MRSMechanicalRepairStationConfig mechanicalRepairStation;

    public MRSCommonConfig() {
        mechanicalRepairStation = nested(0, MRSMechanicalRepairStationConfig::new, Comments.mechanicalRepairStation);
    }

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String mechanicalRepairStation = "Mechanical Repair Station";
    }
}

