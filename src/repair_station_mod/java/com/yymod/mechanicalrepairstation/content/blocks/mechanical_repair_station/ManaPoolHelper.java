package com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;

public final class ManaPoolHelper {

    private static final String MANA_POOL_CLASS = "vazkii.botania.api.mana.IManaPool";
    private static final String GET_CURRENT_MANA = "getCurrentMana";
    private static final String RECEIVE_MANA = "receiveMana";
    private static final String IS_OUTPUTTING = "isOutputtingPower";
    private static final ResourceLocation MANA_FLUID_ID = new ResourceLocation("botania", "mana");
    private static final int MANA_PER_SOURCE_BLOCK = 1000;

    private static final Class<?> POOL_CLASS;
    private static final Method GET_CURRENT_MANA_METHOD;
    private static final Method RECEIVE_MANA_METHOD;
    private static final Method IS_OUTPUTTING_METHOD;
    private static final Fluid MANA_FLUID;

    static {
        Class<?> poolClass = null;
        Method getCurrent = null;
        Method receive = null;
        Method outputting = null;
        try {
            poolClass = Class.forName(MANA_POOL_CLASS);
            getCurrent = poolClass.getMethod(GET_CURRENT_MANA);
            receive = poolClass.getMethod(RECEIVE_MANA, int.class);
            try {
                outputting = poolClass.getMethod(IS_OUTPUTTING);
            } catch (NoSuchMethodException ignored) {
            }
        } catch (Throwable ignored) {
        }
        POOL_CLASS = poolClass;
        GET_CURRENT_MANA_METHOD = getCurrent;
        RECEIVE_MANA_METHOD = receive;
        IS_OUTPUTTING_METHOD = outputting;
        MANA_FLUID = resolveManaFluid();
    }

    private ManaPoolHelper() {
    }

    public static int extractMana(Level level, BlockPos origin, int radius, int maxPerTick, int maxNeeded) {
        int extracted = 0;
        int remaining = Math.max(0, maxNeeded);
        if (remaining <= 0)
            return 0;
        extracted += extractFromPools(level, origin, radius, maxPerTick, remaining);
        remaining = Math.max(0, maxNeeded - extracted);
        if (remaining <= 0)
            return extracted;
        extracted += extractFromFluids(level, origin, radius, maxPerTick - extracted, remaining);
        return extracted;
    }

    private static int extractFromPools(Level level, BlockPos origin, int radius, int maxPerTick, int maxNeeded) {
        if (POOL_CLASS == null || GET_CURRENT_MANA_METHOD == null || RECEIVE_MANA_METHOD == null)
            return 0;
        int toExtract = Math.min(maxPerTick, maxNeeded);
        if (toExtract <= 0)
            return 0;

        int extracted = 0;
        BlockPos min = origin.offset(-radius, -radius, -radius);
        BlockPos max = origin.offset(radius, radius, radius);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (extracted >= toExtract)
                break;
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity == null || !POOL_CLASS.isInstance(entity))
                continue;
            if (IS_OUTPUTTING_METHOD != null) {
                try {
                    Object outputting = IS_OUTPUTTING_METHOD.invoke(entity);
                    if (outputting instanceof Boolean && !((Boolean) outputting))
                        continue;
                } catch (Throwable ignored) {
                }
            }
            int available = getCurrentMana(entity);
            if (available <= 0)
                continue;
            int request = Math.min(available, toExtract - extracted);
            if (request <= 0)
                continue;
            if (receiveMana(entity, -request))
                extracted += request;
        }

        return extracted;
    }

    private static int extractFromFluids(Level level, BlockPos origin, int radius, int maxPerTick, int maxNeeded) {
        int toExtract = Math.min(maxPerTick, maxNeeded);
        if (toExtract <= 0)
            return 0;

        Fluid targetFluid = MANA_FLUID;
        if (targetFluid == null || targetFluid == Fluids.EMPTY)
            return 0;

        int extracted = 0;
        BlockPos min = origin.offset(-radius, -radius, -radius);
        BlockPos max = origin.offset(radius, radius, radius);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (extracted >= toExtract)
                break;
            FluidState state = level.getFluidState(pos);
            if (state.getType() != targetFluid)
                continue;
            if (!state.isSource())
                continue;
            int request = Math.min(MANA_PER_SOURCE_BLOCK, toExtract - extracted);
            if (request <= 0)
                break;
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            extracted += request;
        }

        return extracted;
    }

    private static int getCurrentMana(Object pool) {
        try {
            Object value = GET_CURRENT_MANA_METHOD.invoke(pool);
            if (value instanceof Integer)
                return (int) value;
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static boolean receiveMana(Object pool, int amount) {
        try {
            RECEIVE_MANA_METHOD.invoke(pool, amount);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Fluid resolveManaFluid() {
        Fluid mana = ForgeRegistries.FLUIDS.getValue(MANA_FLUID_ID);
        if (mana == null || mana == Fluids.EMPTY)
            return Fluids.LAVA;
        return mana;
    }
}
