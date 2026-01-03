package com.yymod.mechanicalrepairstation.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class MRSMechanicalRepairStationConfig extends ConfigBase {

    public static final int DEFAULT_ROTATION_BUFFER = 1000;
    public static final int DEFAULT_FE_BUFFER = 1_000_000;
    public static final int DEFAULT_MANA_BUFFER = 100_000;
    public static final int DEFAULT_FE_PER_DURABILITY = 100;
    public static final int DEFAULT_MANA_PER_DURABILITY = 10;
    public static final int DEFAULT_MAX_MANA_EXTRACT_PER_TICK = 1000;
    public static final float DEFAULT_STRESS_IMPACT = 0.0f;
    public static final boolean DEFAULT_DEATH_BREAKS_ITEMS = true;
    public static final int DEFAULT_DEATH_DURABILITY_LOSS_PERCENT = 100;
    public static final List<String> DEFAULT_MATERIAL_JSON_MAPPINGS = List.of(
            "{\"nbt_part\":\"\\\"rar\\\":\\\"common\\\"\"}=minecraft:copper_ingot",
            "{\"nbt_part\":\"\\\"rar\\\":\\\"uncommon\\\"\"}=minecraft:iron_ingot",
            "{\"nbt_part\":\"\\\"rar\\\":\\\"rare\\\"\"}=minecraft:gold_ingot",
            "{\"nbt_part\":\"\\\"rar\\\":\\\"epic\\\"\"}=create:brass_ingot",
            "{\"nbt_part\":\"\\\"rar\\\":\\\"legendary\\\"\"}=minecraft:diamond",
            "{\"nbt_part\":\"\\\"rar\\\":\\\"mythic\\\"\"}=minecraft:diamond_block"
    );

    public final ConfigInt rotationBuffer;
    public final ConfigInt feBuffer;
    public final ConfigInt manaBuffer;
    public final ConfigInt fePerDurability;
    public final ConfigInt manaPerDurability;
    public final ConfigInt maxManaExtractPerTick;
    public final ConfigFloat stressImpact;
    public final ConfigBool deathBreaksItems;
    public final ConfigInt deathDurabilityLossPercent;
    public final CValue<List<? extends String>, ForgeConfigSpec.ConfigValue<List<? extends String>>> materialJsonMappings;

    public MRSMechanicalRepairStationConfig() {
        rotationBuffer = i(DEFAULT_ROTATION_BUFFER, 1, 1_000_000, "rotationBuffer", Comments.rotationBuffer);
        feBuffer = i(DEFAULT_FE_BUFFER, 0, 50_000_000, "feBuffer", Comments.feBuffer);
        manaBuffer = i(DEFAULT_MANA_BUFFER, 0, 1_000_000, "manaBuffer", Comments.manaBuffer);
        fePerDurability = i(DEFAULT_FE_PER_DURABILITY, 1, 10_000, "fePerDurability", Comments.fePerDurability);
        manaPerDurability = i(DEFAULT_MANA_PER_DURABILITY, 0, 10_000, "manaPerDurability", Comments.manaPerDurability);
        maxManaExtractPerTick = i(DEFAULT_MAX_MANA_EXTRACT_PER_TICK, 1, 100_000, "maxManaExtractPerTick", Comments.maxManaExtractPerTick);
        stressImpact = f(DEFAULT_STRESS_IMPACT, 0.0f, 1024.0f, "stressImpact", Comments.stressImpact);
        deathBreaksItems = b(DEFAULT_DEATH_BREAKS_ITEMS, "deathBreaksItems", Comments.deathBreaksItems);
        deathDurabilityLossPercent = i(DEFAULT_DEATH_DURABILITY_LOSS_PERCENT, 0, 100, "deathDurabilityLossPercent",
                Comments.deathDurabilityLossPercent);
        materialJsonMappings = new CValue<>("materialJsonMappings",
                builder -> builder.defineList("materialJsonMappings", DEFAULT_MATERIAL_JSON_MAPPINGS,
                        entry -> entry instanceof String),
                Comments.materialJsonMappings);
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
        static String deathBreaksItems = "If true, death reduces durability of equipped and hotbar items.";
        static String deathDurabilityLossPercent = "Percent of max durability lost on death (0-100).";
        static String materialJsonMappings = "JSON-to-material mappings for repair ingredients (json=namespace:item).";
    }
}

