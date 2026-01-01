package com.yymod.mechanicalworkbench.content.blocks.mechanical_workbench;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MechanicalWorkbenchScreen extends AbstractContainerScreen<MechanicalWorkbenchMenu> {

    private static final ResourceLocation BG = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final int CHARGE_BUTTON_ID = 0;

    private Button chargeButton;

    public MechanicalWorkbenchScreen(MechanicalWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        if (!isManualChargeEnabled())
            return;
        int x = leftPos + 16;
        int y = topPos + 17;
        chargeButton = Button.builder(Component.literal("+"), button -> handleChargeClick())
                .bounds(x, y, 12, 12)
                .build();
        addRenderableWidget(chargeButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderRotationGauge(guiGraphics);
        renderFeGauge(guiGraphics);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (chargeButton == null || minecraft == null || minecraft.player == null)
            return;
        boolean canAfford = minecraft.player.isCreative()
                || minecraft.player.getFoodData().getFoodLevel() > 0;
        boolean hasRoom = menu.getSyncedRotations() < MechanicalWorkbenchBlockEntity.maxRotations();
        chargeButton.active = canAfford && hasRoom;
    }

    private void renderRotationGauge(GuiGraphics guiGraphics) {
        int x = leftPos + 8;
        int y = topPos + 17;
        int width = 6;
        int height = 54;
        int max = MechanicalWorkbenchBlockEntity.maxRotations();
        int rotations = menu.getSyncedRotations();
        boolean enoughForCraft = rotations >= MechanicalWorkbenchBlockEntity.rotationsPerCraft();
        int fillColor = enoughForCraft ? 0xFF72C962 : 0xFFB54A4A;

        guiGraphics.fill(x, y, x + width, y + height, 0xFF2B2B2B);

        if (max <= 0)
            return;

        int innerHeight = height - 2;
        int filled = (int) ((rotations / (float) max) * innerHeight);
        if (filled <= 0)
            return;

        guiGraphics.fill(x + 1, y + height - 1 - filled, x + width - 1, y + height - 1, fillColor);
    }

    private void renderFeGauge(GuiGraphics guiGraphics) {
        int x = leftPos + 16;
        int y = topPos + 31;
        int width = 12;
        int height = 40;
        int max = MechanicalWorkbenchBlockEntity.maxFeBuffer();
        int stored = menu.getSyncedFe();

        guiGraphics.fill(x, y, x + width, y + height, 0xFF2B2B2B);

        if (max <= 0)
            return;

        int innerHeight = height - 2;
        int filled = (int) ((stored / (float) max) * innerHeight);
        if (filled <= 0)
            return;

        guiGraphics.fill(x + 1, y + height - 1 - filled, x + width - 1, y + height - 1, 0xFF4DA3FF);
    }

    private void handleChargeClick() {
        if (minecraft == null || minecraft.gameMode == null)
            return;
        if (!isManualChargeEnabled())
            return;
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, CHARGE_BUTTON_ID);
    }

    private boolean isManualChargeEnabled() {
        return MechanicalWorkbenchBlockEntity.isManualChargeEnabled();
    }
}


