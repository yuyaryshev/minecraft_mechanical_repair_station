package com.yymod.mechanicalworkbench.config;

import net.createmod.catnip.config.ConfigBase;

public class MWCommonConfig extends ConfigBase {

    public final MWMechanicalWorkbenchConfig mechanicalWorkbench;

    public MWCommonConfig() {
        mechanicalWorkbench = nested(0, MWMechanicalWorkbenchConfig::new, Comments.mechanicalWorkbench);
    }

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String mechanicalWorkbench = "Mechanical Workbench";
    }
}
