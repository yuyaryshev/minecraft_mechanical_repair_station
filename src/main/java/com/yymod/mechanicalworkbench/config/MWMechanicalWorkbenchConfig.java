package com.yymod.mechanicalrepairstation.config;

import net.createmod.catnip.config.ConfigBase;

public class MWmechanicalrepairstationConfig extends ConfigBase {

    public static final int DEFAULT_ROTATION_BUFFER = 100;
    public static final int DEFAULT_ROTATIONS_PER_CRAFT = 20;
    public static final int DEFAULT_FE_PER_ROTATION = 10;
    public static final int DEFAULT_FE_BUFFER = DEFAULT_ROTATION_BUFFER * DEFAULT_FE_PER_ROTATION;
    public static final boolean DEFAULT_MANUAL_CHARGE_ENABLED = true;
    public static final int DEFAULT_MANUAL_CHARGE_AMOUNT = 1;
    public static final float DEFAULT_STRESS_IMPACT = 0.0f;

    public final ConfigInt rotationBuffer;
    public final ConfigInt feBuffer;
    public final ConfigInt rotationsPerCraft;
    public final ConfigInt fePerRotation;
    public final ConfigBool manualChargeEnabled;
    public final ConfigInt manualChargeAmount;
    public final ConfigFloat stressImpact;

    public MWmechanicalrepairstationConfig() {
        rotationBuffer = i(DEFAULT_ROTATION_BUFFER, 1, 100000, "rotationBuffer", Comments.rotationBuffer);
        feBuffer = i(DEFAULT_FE_BUFFER, 0, 1000000, "feBuffer", Comments.feBuffer);
        rotationsPerCraft = i(DEFAULT_ROTATIONS_PER_CRAFT, 1, 100000, "rotationsPerCraft", Comments.rotationsPerCraft);
        fePerRotation = i(DEFAULT_FE_PER_ROTATION, 1, 100000, "fePerRotation", Comments.fePerRotation);
        manualChargeEnabled = b(DEFAULT_MANUAL_CHARGE_ENABLED, "manualChargeEnabled", Comments.manualChargeEnabled);
        manualChargeAmount = i(DEFAULT_MANUAL_CHARGE_AMOUNT, 1, 100000, "manualChargeAmount", Comments.manualChargeAmount);
        stressImpact = f(DEFAULT_STRESS_IMPACT, 0.0f, 1024.0f, "stressImpact", Comments.stressImpact);
    }

    @Override
    public String getName() {
        return "mechanicalrepairstation";
    }

    private static class Comments {
        static String rotationBuffer = "Maximum stored rotations.";
        static String feBuffer = "Maximum stored FE.";
        static String rotationsPerCraft = "Rotations consumed per craft.";
        static String fePerRotation = "FE converted per rotation.";
        static String manualChargeEnabled = "Whether the manual charge button is available.";
        static String manualChargeAmount = "Rotations added per manual charge.";
        static String stressImpact = "Stress impact while charging.";
    }
}

