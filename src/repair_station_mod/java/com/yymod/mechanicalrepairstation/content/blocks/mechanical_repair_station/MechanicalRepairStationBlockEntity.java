package com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.yymod.mechanicalrepairstation.config.MRSConfigs;
import com.yymod.mechanicalrepairstation.config.MRSMechanicalRepairStationConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class MechanicalRepairStationBlockEntity extends KineticBlockEntity implements MenuProvider {

    public static final String TAG_UPGRADE_LEVEL = "RepairStationUpgradeLevel";
    public static final String TAG_BASE_MAX_DAMAGE = "RepairStationBaseMaxDamage";
    public static final int INVENTORY_SIZE = 12;
    public static final int TARGET_SLOT = 0;
    public static final int MATERIAL_SLOT_START = 1;
    public static final int MATERIAL_SLOT_END = 10;
    public static final int OUTPUT_SLOT = 11;
    private static final int ROTATIONS_PER_DURABILITY = 1;
    private static final int MANA_SEARCH_RADIUS = 3;
    private static final int MAX_FE_EXTRACT_PER_TICK = 1000;

    private ItemStackHandler inventory;
    private LazyOptional<IItemHandler> itemHandler;

    private float rotationBuffer;
    private int feBuffer;
    private int manaBuffer;

    public MechanicalRepairStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        rotationBuffer = 0f;
        feBuffer = 0;
        manaBuffer = 0;
        inventory = createHandler(INVENTORY_SIZE);
        itemHandler = LazyOptional.of(() -> inventory);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide)
            return;
        float speed = Math.abs(getSpeed());
        if (speed > 0f) {
            float rotationsThisTick = speed / 1200f;
            if (rotationsThisTick > 0f) {
                float updated = Math.min(maxRotations(), rotationBuffer + rotationsThisTick);
                if (updated != rotationBuffer) {
                    rotationBuffer = updated;
                    setChanged();
                    sendData();
                }
            }
        }

        pullEnergyFromNeighbors();
        pullManaFromNearbyPools();
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public float getRotationBuffer() {
        return rotationBuffer;
    }

    public int getFeBuffer() {
        return feBuffer;
    }

    public int getManaBuffer() {
        return manaBuffer;
    }

    public boolean handleRepair(Player player) {
        if (level == null || level.isClientSide)
            return false;
        ItemStack target = inventory.getStackInSlot(TARGET_SLOT);
        if (target.isEmpty() || !target.isDamageableItem())
            return false;
        int damage = target.getDamageValue();
        if (damage <= 0)
            return false;

        int maxRepair = damage;
        boolean enchanted = target.isEnchanted();
        int manaPerDurability = manaPerDurability();
        if (enchanted && manaPerDurability > 0)
            maxRepair = Math.min(maxRepair, manaBuffer / manaPerDurability);
        if (maxRepair <= 0)
            return false;

        boolean freeRepair = isFreeRepairCandidate(target);
        double durabilityPerMaterial = target.getMaxDamage() / 3.0;
        if (!freeRepair) {
            int availableMaterials = countMaterials(target);
            int maxByMaterials = (int) Math.floor(availableMaterials * durabilityPerMaterial);
            maxRepair = Math.min(maxRepair, maxByMaterials);
        }
        if (maxRepair <= 0)
            return false;

        int fePerDurability = fePerDurability();
        int maxFeRepair = fePerDurability > 0 ? feBuffer / fePerDurability : 0;
        int feRepair = Math.min(maxRepair, maxFeRepair);
        int remaining = maxRepair - feRepair;
        int rotRepair = 0;
        if (remaining > 0 && (feBuffer - (feRepair * fePerDurability)) < fePerDurability)
            rotRepair = Math.min(remaining, Mth.floor(rotationBuffer));
        int totalRepair = feRepair + rotRepair;
        if (totalRepair <= 0)
            return false;

        if (!freeRepair) {
            int materialsToConsume = (int) Math.ceil(totalRepair / durabilityPerMaterial);
            if (materialsToConsume > 0) {
                int consumed = consumeMaterials(target, materialsToConsume);
                if (consumed < materialsToConsume)
                    return false;
            }
        }

        if (fePerDurability > 0 && feRepair > 0)
            feBuffer = Math.max(0, feBuffer - (feRepair * fePerDurability));
        if (rotRepair > 0)
            rotationBuffer = Math.max(0f, rotationBuffer - rotRepair * ROTATIONS_PER_DURABILITY);
        if (enchanted && manaPerDurability > 0 && totalRepair > 0)
            manaBuffer = Math.max(0, manaBuffer - (totalRepair * manaPerDurability));

        target.setDamageValue(Math.max(0, damage - totalRepair));
        inventory.setStackInSlot(TARGET_SLOT, target);

        playRepairSound();
        setChanged();
        sendData();
        return true;
    }

    public boolean handleUpgrade(Player player) {
        if (level == null || level.isClientSide)
            return false;
        ItemStack target = inventory.getStackInSlot(TARGET_SLOT);
        if (target.isEmpty() || !target.isDamageableItem())
            return false;

        Item willItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("yyitems", "will_of_durability"));
        if (willItem == null)
            return false;

        int availableMaterials = countMaterials(target);
        if (availableMaterials <= 0)
            return false;

        CompoundTag tag = target.getOrCreateTag();
        int currentLevel = tag.getInt(TAG_UPGRADE_LEVEL);
        int materialsRequired = 3 + currentLevel;
        if (availableMaterials < materialsRequired)
            return false;

        if (!consumeWillOfDurability(willItem))
            return false;
        if (consumeMaterials(target, materialsRequired) < materialsRequired)
            return false;

        int baseMax = tag.contains(TAG_BASE_MAX_DAMAGE, Tag.TAG_INT)
                ? tag.getInt(TAG_BASE_MAX_DAMAGE)
                : target.getItem().getMaxDamage();
        tag.putInt(TAG_BASE_MAX_DAMAGE, baseMax);
        tag.putInt(TAG_UPGRADE_LEVEL, currentLevel + 1);

        playUpgradeSound();
        setChanged();
        sendData();
        return true;
    }

    private int countMaterials(ItemStack target) {
        int count = 0;
        for (int slot = MATERIAL_SLOT_START; slot <= getMaterialSlotEnd(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;
            if (isRepairMaterial(target, stack))
                count += stack.getCount();
        }
        return count;
    }

    private int consumeMaterials(ItemStack target, int count) {
        int remaining = count;
        for (int slot = MATERIAL_SLOT_START; slot <= getMaterialSlotEnd(); slot++) {
            if (remaining <= 0)
                break;
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty() || !isRepairMaterial(target, stack))
                continue;
            int take = Math.min(remaining, stack.getCount());
            stack.shrink(take);
            remaining -= take;
            if (stack.isEmpty())
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return count - remaining;
    }

    private static boolean isRepairMaterial(ItemStack target, ItemStack material) {
        return target.getItem().isValidRepairItem(target, material);
    }

    private boolean consumeWillOfDurability(Item willItem) {
        for (int slot = MATERIAL_SLOT_START; slot <= getMaterialSlotEnd(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty() || !stack.is(willItem))
                continue;
            stack.shrink(1);
            if (stack.isEmpty())
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    private int getMaterialSlotEnd() {
        return Math.min(MATERIAL_SLOT_END, inventory.getSlots() - 1);
    }

    private void pullEnergyFromNeighbors() {
        if (level == null || level.isClientSide)
            return;
        int maxRotations = maxRotations();
        int maxFeBuffer = maxFeBuffer();
        if (rotationBuffer >= maxRotations && feBuffer >= maxFeBuffer)
            return;

        int remainingBuffer = Math.max(0, maxFeBuffer - feBuffer);
        int feToExtract = Math.min(MAX_FE_EXTRACT_PER_TICK, remainingBuffer);
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

    private void pullManaFromNearbyPools() {
        if (level == null || level.isClientSide)
            return;
        int maxManaBuffer = maxManaBuffer();
        if (manaBuffer >= maxManaBuffer)
            return;
        int remainingBuffer = Math.max(0, maxManaBuffer - manaBuffer);
        int maxExtract = maxManaExtractPerTick();
        int extracted = ManaPoolHelper.extractMana(level, worldPosition, MANA_SEARCH_RADIUS, maxExtract, remainingBuffer);
        if (extracted > 0) {
            manaBuffer = Math.min(maxManaBuffer, manaBuffer + extracted);
            setChanged();
            sendData();
        }
    }

    private static boolean isFreeRepairCandidate(ItemStack stack) {
        if (stack.isEnchanted())
            return false;
        Item item = stack.getItem();
        if (item instanceof ArmorItem armor)
            return armor.getMaterial() == ArmorMaterials.LEATHER;
        if (item instanceof TieredItem tiered) {
            Tier tier = tiered.getTier();
            return tier == Tiers.WOOD || tier == Tiers.STONE;
        }
        return false;
    }

    private void playRepairSound() {
        if (level == null)
            return;
        level.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.9f, 1.0f);
    }

    private void playUpgradeSound() {
        if (level == null)
            return;
        level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.9f, 1.0f);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putFloat("RotationBuffer", rotationBuffer);
        compound.putInt("FEBuffer", feBuffer);
        compound.putInt("ManaBuffer", manaBuffer);
        compound.put("Inventory", inventory.serializeNBT());
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        rotationBuffer = compound.getFloat("RotationBuffer");
        feBuffer = compound.getInt("FEBuffer");
        manaBuffer = compound.getInt("ManaBuffer");
        if (compound.contains("Inventory"))
            inventory.deserializeNBT(compound.getCompound("Inventory"));
        resizeInventoryIfNeeded();
        rotationBuffer = Math.min(rotationBuffer, maxRotations());
        feBuffer = Math.min(feBuffer, maxFeBuffer());
        manaBuffer = Math.min(manaBuffer, maxManaBuffer());
        super.read(compound, clientPacket);
    }

    public static int maxRotations() {
        if (MRSConfigs.common() == null)
            return MRSMechanicalRepairStationConfig.DEFAULT_ROTATION_BUFFER;
        return Math.max(1, MRSConfigs.common().mechanicalRepairStation.rotationBuffer.get());
    }

    public static int maxFeBuffer() {
        if (MRSConfigs.common() == null)
            return MRSMechanicalRepairStationConfig.DEFAULT_FE_BUFFER;
        return Math.max(0, MRSConfigs.common().mechanicalRepairStation.feBuffer.get());
    }

    public static int maxManaBuffer() {
        if (MRSConfigs.common() == null)
            return MRSMechanicalRepairStationConfig.DEFAULT_MANA_BUFFER;
        return Math.max(0, MRSConfigs.common().mechanicalRepairStation.manaBuffer.get());
    }

    public static int fePerDurability() {
        if (MRSConfigs.common() == null)
            return MRSMechanicalRepairStationConfig.DEFAULT_FE_PER_DURABILITY;
        return Math.max(1, MRSConfigs.common().mechanicalRepairStation.fePerDurability.get());
    }

    public static int manaPerDurability() {
        if (MRSConfigs.common() == null)
            return MRSMechanicalRepairStationConfig.DEFAULT_MANA_PER_DURABILITY;
        return Math.max(0, MRSConfigs.common().mechanicalRepairStation.manaPerDurability.get());
    }

    public static int maxManaExtractPerTick() {
        if (MRSConfigs.common() == null)
            return MRSMechanicalRepairStationConfig.DEFAULT_MAX_MANA_EXTRACT_PER_TICK;
        return Math.max(1, MRSConfigs.common().mechanicalRepairStation.maxManaExtractPerTick.get());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.yy_mechanical_repair_station.mechanical_repair_station");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new MechanicalRepairStationMenu(id, playerInventory, ContainerLevelAccess.create(level, worldPosition), this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = LazyOptional.of(() -> inventory);
    }

    private ItemStackHandler createHandler(int size) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                sendData();
            }
        };
    }

    private void resizeInventoryIfNeeded() {
        if (inventory.getSlots() >= INVENTORY_SIZE)
            return;
        ItemStackHandler resized = createHandler(INVENTORY_SIZE);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            resized.setStackInSlot(slot, inventory.getStackInSlot(slot));
        }
        inventory = resized;
        if (itemHandler != null)
            itemHandler.invalidate();
        itemHandler = LazyOptional.of(() -> inventory);
    }
}
