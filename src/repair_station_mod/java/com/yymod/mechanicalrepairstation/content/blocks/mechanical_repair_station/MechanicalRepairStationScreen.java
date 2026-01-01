package com.yymod.mechanicalrepairstation.content.blocks.mechanical_repair_station;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MechanicalRepairStationScreen extends AbstractContainerScreen<MechanicalRepairStationMenu> {

    private static final ResourceLocation BG = new ResourceLocation("yy_mechanical_repair_station",
            "textures/screens/mechanical_repair_station.png");
    private static final ResourceLocation BG_OVERLAY = new ResourceLocation("yy_mechanical_repair_station",
            "textures/screens/mechanical_repair_station_overlay.png");
    private static final int REPAIR_BUTTON_ID = 0;
    private static final int UPGRADE_BUTTON_ID = 1;
    private static final int GAUGE_X = 3;
    private static final int GAUGE_Y = 4;
    private static final int UPGRADE_Y = 69;

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
        int upgradeX = leftPos + 4;
        int upgradeY = topPos + UPGRADE_Y - 2;
        int repairX = leftPos + 134;
        int repairY = topPos + UPGRADE_Y - 16;
        repairButton = Button.builder(Component.literal("R.All!"), button -> handleClick(REPAIR_BUTTON_ID))
                .bounds(repairX, repairY, 36, 28)
                .build();
        upgradeButton = Button.builder(Component.literal("Upg"), button -> handleClick(UPGRADE_BUTTON_ID))
                .bounds(upgradeX, upgradeY, 36, 14)
                .build();
        addRenderableWidget(repairButton);
        addRenderableWidget(upgradeButton);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, Component.literal("Ready"), 43, 34, 0xFF3C4C5C, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        guiGraphics.blit(BG_OVERLAY, leftPos + 2, topPos + 2, 0, 0, 170, 80, 170, 80);
        RenderSystem.disableBlend();
        int gaugeHeight = (UPGRADE_Y - 3) - GAUGE_Y;
        int gaugeBottom = topPos + GAUGE_Y + gaugeHeight;
        renderGauge(guiGraphics, leftPos + GAUGE_X, gaugeBottom, 6, gaugeHeight,
                menu.getSyncedRotations(), MechanicalRepairStationBlockEntity.maxRotations(), 0xFF72C962);
        renderGauge(guiGraphics, leftPos + GAUGE_X + 8, gaugeBottom, 6, gaugeHeight,
                menu.getSyncedFe(), MechanicalRepairStationBlockEntity.maxFeBuffer(), 0xFF4DA3FF);
        renderGauge(guiGraphics, leftPos + GAUGE_X + 16, gaugeBottom, 6, gaugeHeight,
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
