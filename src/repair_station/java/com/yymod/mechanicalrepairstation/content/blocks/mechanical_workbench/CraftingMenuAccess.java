package com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.level.Level;

public class CraftingMenuAccess extends CraftingMenu {

    public CraftingMenuAccess(int id, Inventory inventory, ContainerLevelAccess access) {
        super(id, inventory, access);
    }

    public static void updateCraftingResult(AbstractContainerMenu menu, Level level, Player player,
                                            CraftingContainer craftSlots, ResultContainer resultSlots) {
        slotChangedCraftingGrid(menu, level, player, craftSlots, resultSlots);
    }
}


