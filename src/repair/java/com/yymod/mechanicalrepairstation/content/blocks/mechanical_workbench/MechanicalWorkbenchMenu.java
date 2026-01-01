package com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodData;

import com.yymod.mechanicalworkbench.registries.MWMenus;

public class MechanicalWorkbenchMenu extends RecipeBookMenu<CraftingContainer> {

    private static final int RESULT_SLOT_INDEX = 0;
    private static final int GRID_START = 1;
    private static final int GRID_END = 10;
    private static final int PLAYER_INV_START = 10;
    private static final int PLAYER_INV_END = 37;
    private static final int HOTBAR_START = 37;
    private static final int HOTBAR_END = 46;

    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots;
    private final ContainerLevelAccess access;
    private final Player player;
    private final MechanicalWorkbenchBlockEntity workbench;
    private int syncedRotations;
    private int syncedFe;
    private int lastRotationForResult;

    public MechanicalWorkbenchMenu(int id, Inventory inventory, ContainerLevelAccess access,
                                   MechanicalWorkbenchBlockEntity workbench) {
        super(MWMenus.MECHANICAL_WORKBENCH.get(), id);
        this.access = access;
        this.player = inventory.player;
        this.workbench = workbench;
        craftSlots = new TransientCraftingContainer(this, 3, 3);
        resultSlots = new ResultContainer();

        this.addSlot(new PoweredResultSlot(player, craftSlots, resultSlots, RESULT_SLOT_INDEX, 124, 35, workbench));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(craftSlots, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                if (workbench == null)
                    return syncedRotations;
                return Mth.floor(workbench.getRotationBuffer());
            }

            @Override
            public void set(int value) {
                syncedRotations = value;
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                if (workbench == null)
                    return syncedFe;
                return workbench.getFeBuffer();
            }

            @Override
            public void set(int value) {
                syncedFe = value;
            }
        });
    }

    public static MechanicalWorkbenchMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        MechanicalWorkbenchBlockEntity workbench = be instanceof MechanicalWorkbenchBlockEntity
                ? (MechanicalWorkbenchBlockEntity) be
                : null;
        return new MechanicalWorkbenchMenu(id, inventory, ContainerLevelAccess.create(inventory.player.level(), pos), workbench);
    }

    public int getSyncedRotations() {
        return syncedRotations;
    }

    public int getSyncedFe() {
        return syncedFe;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != 0)
            return super.clickMenuButton(player, id);
        if (!MechanicalWorkbenchBlockEntity.isManualChargeEnabled())
            return false;
        if (player.level().isClientSide || workbench == null)
            return false;
        if (workbench.getRotationBuffer() >= MechanicalWorkbenchBlockEntity.maxRotations())
            return false;
        if (!player.isCreative()) {
            FoodData foodData = player.getFoodData();
            int food = foodData.getFoodLevel();
            if (food <= 0)
                return false;
            foodData.setFoodLevel(food - 1);
            if (foodData.getSaturationLevel() > foodData.getFoodLevel())
                foodData.setSaturation(foodData.getFoodLevel());
        }
        int amount = MechanicalWorkbenchBlockEntity.manualChargeAmount();
        if (amount <= 0)
            return false;
        workbench.addManualRotations(amount);
        return true;
    }


    @Override
    public void slotsChanged(Container container) {
        access.execute((level, pos) -> {
            if (workbench != null && !workbench.canCraft()) {
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }
            CraftingMenuAccess.updateCraftingResult(this, level, player, craftSlots, resultSlots);
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player.level().isClientSide || workbench == null)
            return;
        int rotations = Mth.floor(workbench.getRotationBuffer());
        if (rotations == lastRotationForResult)
            return;
        lastRotationForResult = rotations;
        slotsChanged(craftSlots);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, craftSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        if (workbench == null)
            return false;
        return stillValid(access, player, workbench.getBlockState().getBlock());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            if (index == RESULT_SLOT_INDEX) {
                if (workbench != null && !workbench.canCraft())
                    return ItemStack.EMPTY;
                access.execute((level, pos) -> slotStack.getItem().onCraftedBy(slotStack, level, player));
                if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, HOTBAR_END, true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(slotStack, itemstack);
            } else if (index >= PLAYER_INV_START && index < HOTBAR_END) {
                if (!this.moveItemStackTo(slotStack, GRID_START, GRID_END, false)) {
                    if (index < PLAYER_INV_END) {
                        if (!this.moveItemStackTo(slotStack, HOTBAR_START, HOTBAR_END, false))
                            return ItemStack.EMPTY;
                    } else if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
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

    @Override
    public void fillCraftSlotsStackedContents(net.minecraft.world.entity.player.StackedContents stackedContents) {
        craftSlots.fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        craftSlots.clearContent();
        resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(Recipe<? super CraftingContainer> recipe) {
        return recipe.matches(craftSlots, player.level());
    }

    @Override
    public int getResultSlotIndex() {
        return RESULT_SLOT_INDEX;
    }

    @Override
    public int getGridWidth() {
        return 3;
    }

    @Override
    public int getGridHeight() {
        return 3;
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex != getResultSlotIndex();
    }

    private static class PoweredResultSlot extends ResultSlot {

        private final MechanicalWorkbenchBlockEntity workbench;

        public PoweredResultSlot(Player player, CraftingContainer craftSlots, ResultContainer resultSlots, int index,
                                 int x, int y, MechanicalWorkbenchBlockEntity workbench) {
            super(player, craftSlots, resultSlots, index, x, y);
            this.workbench = workbench;
        }

        @Override
        public boolean mayPickup(Player player) {
            if (player.level().isClientSide)
                return true;
            return workbench != null && workbench.canCraft();
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            if (!player.level().isClientSide && workbench != null)
                workbench.consumeCraftingRotations(1);
        }
    }
}


