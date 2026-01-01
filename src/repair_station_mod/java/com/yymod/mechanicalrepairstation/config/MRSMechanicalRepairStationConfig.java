package com.yymod.mechanicalrepairstation.config;

import net.createmod.catnip.config.ConfigBase;

public class MRSMechanicalRepairStationConfig extends ConfigBase {

    public static final int DEFAULT_ROTATION_BUFFER = 1000;
    public static final int DEFAULT_FE_BUFFER = 1_000_000;
    public static final int DEFAULT_MANA_BUFFER = 100_000;
    public static final int DEFAULT_FE_PER_DURABILITY = 100;
    public static final int DEFAULT_MANA_PER_DURABILITY = 10;
    public static final int DEFAULT_MAX_MANA_EXTRACT_PER_TICK = 1000;
    public static final float DEFAULT_STRESS_IMPACT = 0.0f;

    public final ConfigInt rotationBuffer;
    public final ConfigInt feBuffer;
    public final ConfigInt manaBuffer;
    public final ConfigInt fePerDurability;
    public final ConfigInt manaPerDurability;
    public final ConfigInt maxManaExtractPerTick;
    public final ConfigFloat stressImpact;

    public MRSMechanicalRepairStationConfig() {
        rotationBuffer = i(DEFAULT_ROTATION_BUFFER, 1, 1_000_000, "rotationBuffer", Comments.rotationBuffer);
        feBuffer = i(DEFAULT_FE_BUFFER, 0, 50_000_000, "feBuffer", Comments.feBuffer);
        manaBuffer = i(DEFAULT_MANA_BUFFER, 0, 1_000_000, "manaBuffer", Comments.manaBuffer);
        fePerDurability = i(DEFAULT_FE_PER_DURABILITY, 1, 10_000, "fePerDurability", Comments.fePerDurability);
        manaPerDurability = i(DEFAULT_MANA_PER_DURABILITY, 0, 10_000, "manaPerDurability", Comments.manaPerDurability);
        maxManaExtractPerTick = i(DEFAULT_MAX_MANA_EXTRACT_PER_TICK, 1, 100_000, "maxManaExtractPerTick", Comments.maxManaExtractPerTick);
        stressImpact = f(DEFAULT_STRESS_IMPACT, 0.0f, 1024.0f, "stressImpact", Comments.stressImpact);
    }

    @Override
    public String getName() {
        return "mechanicalRepairStation";
    }

    private static class Comments {
        static String rotationBuffer = "Maximum stored rotations.";
        static String feBuffer = "Maximum stored FE.";
        static String manaBuffer = "Maximum stored mana.";
        static String fePerDurability = "FE consumed per durability repaired.";
        static String manaPerDurability = "Mana consumed per durability repaired on enchanted items.";
        static String maxManaExtractPerTick = "Maximum mana extracted per tick from nearby pools.";
        static String stressImpact = "Stress impact while charging.";
    }
}

