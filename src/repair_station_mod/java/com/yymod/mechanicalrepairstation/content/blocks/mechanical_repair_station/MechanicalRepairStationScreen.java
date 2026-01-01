package com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MechanicalRepairStationScreen extends AbstractContainerScreen<MechanicalRepairStationMenu> {

    private static final ResourceLocation BG = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final int REPAIR_BUTTON_ID = 0;
    private static final int UPGRADE_BUTTON_ID = 1;

    private Button repairButton;
    private Button upgradeButton;

    public MechanicalRepairStationScreen(MechanicalRepairStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos + 8;
        int y = topPos + 20;
        repairButton = Button.builder(Component.literal("Repair"), button -> handleClick(REPAIR_BUTTON_ID))
                .bounds(x, y, 50, 14)
                .build();
        upgradeButton = Button.builder(Component.literal("Upgrade"), button -> handleClick(UPGRADE_BUTTON_ID))
                .bounds(x, y + 18, 50, 14)
                .build();
        addRenderableWidget(repairButton);
        addRenderableWidget(upgradeButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderGauge(guiGraphics, leftPos + 8, topPos + 58, 6, 50,
                menu.getSyncedRotations(), MechanicalRepairStationBlockEntity.maxRotations(), 0xFF72C962);
        renderGauge(guiGraphics, leftPos + 16, topPos + 58, 6, 50,
                menu.getSyncedFe(), MechanicalRepairStationBlockEntity.maxFeBuffer(), 0xFF4DA3FF);
        renderGauge(guiGraphics, leftPos + 24, topPos + 58, 6, 50,
                menu.getSyncedMana(), MechanicalRepairStationBlockEntity.maxManaBuffer(), 0xFF6FCFA5);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (repairButton != null)
            repairButton.active = true;
        if (upgradeButton != null)
            upgradeButton.active = true;
    }

    private void renderGauge(GuiGraphics guiGraphics, int x, int bottom, int width, int height,
                             int value, int max, int color) {
        int y = bottom - height;
        guiGraphics.fill(x, y, x + width, y + height, 0xFF2B2B2B);
        if (max <= 0 || value <= 0)
            return;
        int innerHeight = height - 2;
        int filled = (int) ((value / (float) max) * innerHeight);
        if (filled <= 0)
            return;
        guiGraphics.fill(x + 1, y + height - 1 - filled, x + width - 1, y + height - 1, color);
    }

    private void handleClick(int id) {
        if (minecraft == null || minecraft.gameMode == null)
            return;
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }
}
