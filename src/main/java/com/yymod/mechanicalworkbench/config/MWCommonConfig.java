package com.yymod.mechanicalrepairstation.config;

import net.createmod.catnip.config.ConfigBase;

public class MWCommonConfig extends ConfigBase {

    public final MWmechanicalrepairstationConfig mechanicalrepairstation;

    public MWCommonConfig() {
        mechanicalrepairstation = nested(0, MWmechanicalrepairstationConfig::new, Comments.mechanicalrepairstation);
    }

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String mechanicalrepairstation = "Mechanical Repair Station";
    }
}

