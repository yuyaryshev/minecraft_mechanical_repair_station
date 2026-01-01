package com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.yymod.mechanicalworkbench.config.MWConfigs;
import com.yymod.mechanicalworkbench.config.MWMechanicalWorkbenchConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

public class MechanicalWorkbenchBlockEntity extends KineticBlockEntity implements MenuProvider {

    public static final int MAX_FE_EXTRACT_PER_TICK = 100;

    private float rotationBuffer;
    private int feBuffer;

    public MechanicalWorkbenchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        rotationBuffer = 0f;
        feBuffer = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide)
            return;
        float speed = Math.abs(getSpeed());
        if (speed <= 0)
            return;

        float rotationsThisTick = speed / 1200f;
        if (rotationsThisTick <= 0f)
            return;

        float updated = Math.min(maxRotations(), rotationBuffer + rotationsThisTick);
        if (updated != rotationBuffer) {
            rotationBuffer = updated;
            setChanged();
        }

        pullEnergyFromNeighbors();
        convertFeToRotations();
    }

    public boolean canCraft() {
        return rotationBuffer >= rotationsPerCraft();
    }

    public void consumeCraftingRotations(int crafts) {
        if (crafts <= 0)
            return;
        rotationBuffer = Math.max(0f, rotationBuffer - (rotationsPerCraft() * crafts));
        setChanged();
        sendData();
    }

    public void addManualRotations(int rotations) {
        if (rotations <= 0)
            return;
        rotationBuffer = Math.min(maxRotations(), rotationBuffer + rotations);
        setChanged();
        sendData();
    }

    public float getRotationBuffer() {
        return rotationBuffer;
    }

    public int getFeBuffer() {
        return feBuffer;
    }

    private void pullEnergyFromNeighbors() {
        if (level == null || level.isClientSide)
            return;
        int maxRotations = maxRotations();
        int maxFeBuffer = maxFeBuffer();
        int fePerRotation = fePerRotation();
        if (fePerRotation <= 0)
            return;
        if (rotationBuffer >= maxRotations && feBuffer >= maxFeBuffer)
            return;

        int rotationsNeeded = Mth.ceil(maxRotations - rotationBuffer);
        int feNeeded = rotationsNeeded * fePerRotation;
        int remainingBuffer = Math.max(0, maxFeBuffer - feBuffer);
        int feToExtract = Math.min(feNeeded, Math.min(MAX_FE_EXTRACT_PER_TICK, remainingBuffer));
        if (feToExtract <= 0)
            return;

        int extracted = 0;
        for (Direction direction : Direction.values()) {
            if (extracted >= feToExtract)
                break;
            var neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null)
                continue;
            var cap = neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if (!cap.isPresent())
                continue;
            IEnergyStorage storage = cap.orElse(null);
            if (storage == null || !storage.canExtract())
                continue;
            int request = feToExtract - extracted;
            int got = storage.extractEnergy(request, false);
            if (got > 0)
                extracted += got;
        }

        if (extracted > 0) {
            feBuffer = Math.min(maxFeBuffer, feBuffer + extracted);
            setChanged();
            sendData();
        }
    }

    private void convertFeToRotations() {
        int maxRotations = maxRotations();
        int fePerRotation = fePerRotation();
        if (fePerRotation <= 0)
            return;
        if (feBuffer <= 0 || rotationBuffer >= maxRotations)
            return;
        int rotationsFromFe = feBuffer / fePerRotation;
        if (rotationsFromFe <= 0)
            return;
        int maxAdd = Mth.floor(maxRotations - rotationBuffer);
        int rotationsToAdd = Math.min(rotationsFromFe, maxAdd);
        if (rotationsToAdd <= 0)
            return;
        rotationBuffer = Math.min(maxRotations, rotationBuffer + rotationsToAdd);
        feBuffer -= rotationsToAdd * fePerRotation;
        setChanged();
        sendData();
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putFloat("RotationBuffer", rotationBuffer);
        compound.putInt("FEBuffer", feBuffer);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        rotationBuffer = compound.getFloat("RotationBuffer");
        feBuffer = compound.getInt("FEBuffer");
        rotationBuffer = Math.min(rotationBuffer, maxRotations());
        feBuffer = Math.min(feBuffer, maxFeBuffer());
        super.read(compound, clientPacket);
    }

    public static int maxRotations() {
        if (MWConfigs.common() == null)
            return MWMechanicalWorkbenchConfig.DEFAULT_ROTATION_BUFFER;
        return Math.max(1, MWConfigs.common().mechanicalWorkbench.rotationBuffer.get());
    }

    public static int maxFeBuffer() {
        if (MWConfigs.common() == null)
            return MWMechanicalWorkbenchConfig.DEFAULT_FE_BUFFER;
        return Math.max(0, MWConfigs.common().mechanicalWorkbench.feBuffer.get());
    }

    public static int rotationsPerCraft() {
        if (MWConfigs.common() == null)
            return MWMechanicalWorkbenchConfig.DEFAULT_ROTATIONS_PER_CRAFT;
        return Math.max(1, MWConfigs.common().mechanicalWorkbench.rotationsPerCraft.get());
    }

    public static int fePerRotation() {
        if (MWConfigs.common() == null)
            return MWMechanicalWorkbenchConfig.DEFAULT_FE_PER_ROTATION;
        return Math.max(0, MWConfigs.common().mechanicalWorkbench.fePerRotation.get());
    }

    public static boolean isManualChargeEnabled() {
        if (MWConfigs.common() == null)
            return MWMechanicalWorkbenchConfig.DEFAULT_MANUAL_CHARGE_ENABLED;
        return MWConfigs.common().mechanicalWorkbench.manualChargeEnabled.get();
    }

    public static int manualChargeAmount() {
        if (MWConfigs.common() == null)
            return MWMechanicalWorkbenchConfig.DEFAULT_MANUAL_CHARGE_AMOUNT;
        return Math.max(0, MWConfigs.common().mechanicalWorkbench.manualChargeAmount.get());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.yy_mechanical_workbench.mechanical_workbench");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new MechanicalWorkbenchMenu(id, playerInventory, ContainerLevelAccess.create(level, worldPosition), this);
    }
}



