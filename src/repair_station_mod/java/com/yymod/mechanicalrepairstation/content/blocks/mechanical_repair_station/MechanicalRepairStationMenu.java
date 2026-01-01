package com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station;

import com.yymod.mechanicalrepairstation.registries.MRSMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MechanicalRepairStationMenu extends AbstractContainerMenu {

    private static final int MACHINE_SLOTS = MechanicalRepairStationBlockEntity.INVENTORY_SIZE;
    private static final int PLAYER_INV_START = MACHINE_SLOTS;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_END = PLAYER_INV_END + 9;

    private final ContainerLevelAccess access;
    private final MechanicalRepairStationBlockEntity station;
    private int syncedRotations;
    private int syncedFe;
    private int syncedMana;

    public MechanicalRepairStationMenu(int id, Inventory inventory, ContainerLevelAccess access,
                                       MechanicalRepairStationBlockEntity station) {
        super(MRSMenus.MECHANICAL_REPAIR_STATION.get(), id);
        this.access = access;
        this.station = station;

        ItemStackHandler handler = station != null
                ? station.getInventory()
                : new ItemStackHandler(MechanicalRepairStationBlockEntity.INVENTORY_SIZE);

        addSlot(new SlotItemHandler(handler, MechanicalRepairStationBlockEntity.TARGET_SLOT, 26, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.isDamageableItem();
            }
        });

        int startX = 62;
        int startY = 18;
        int slot = MechanicalRepairStationBlockEntity.MATERIAL_SLOT_START;
        int maxSlot = Math.min(MechanicalRepairStationBlockEntity.MATERIAL_SLOT_END, handler.getSlots() - 1);
        int maxMaterialSlots = Math.max(0, maxSlot - slot + 1);
        for (int i = 0; i < 10 && i < maxMaterialSlots; i++) {
            int row = i / 5;
            int col = i % 5;
            addSlot(new SlotItemHandler(handler, slot++, startX + col * 18, startY + row * 18));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                if (station == null)
                    return syncedRotations;
                return Mth.floor(station.getRotationBuffer());
            }

            @Override
            public void set(int value) {
                syncedRotations = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                if (station == null)
                    return syncedFe;
                return station.getFeBuffer();
            }

            @Override
            public void set(int value) {
                syncedFe = value;
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                if (station == null)
                    return syncedMana;
                return station.getManaBuffer();
            }

            @Override
            public void set(int value) {
                syncedMana = value;
            }
        });
    }

    public static MechanicalRepairStationMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        MechanicalRepairStationBlockEntity station = be instanceof MechanicalRepairStationBlockEntity
                ? (MechanicalRepairStationBlockEntity) be
                : null;
        return new MechanicalRepairStationMenu(id, inventory, ContainerLevelAccess.create(inventory.player.level(), pos), station);
    }

    public int getSyncedRotations() {
        return syncedRotations;
    }

    public int getSyncedFe() {
        return syncedFe;
    }

    public int getSyncedMana() {
        return syncedMana;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (station == null)
            return false;
        if (player.level().isClientSide)
            return true;
        if (id == 0)
            return station.handleRepair(player);
        if (id == 1)
            return station.handleUpgrade(player);
        return super.clickMenuButton(player, id);
    }

    @Override
    public boolean stillValid(Player player) {
        if (station == null)
            return false;
        return stillValid(access, player, station.getBlockState().getBlock());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            if (index < MACHINE_SLOTS) {
                if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, HOTBAR_END, true))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(slotStack, 0, MACHINE_SLOTS, false)) {
                if (index < PLAYER_INV_END) {
                    if (!this.moveItemStackTo(slotStack, PLAYER_INV_END, HOTBAR_END, false))
                        return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();

            if (slotStack.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, slotStack);
        }

        return itemstack;
    }
}
