package net.jmb19905.niftycarts.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class ChestScreen extends ContainerScreen {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private final int containerRows;

    public ChestScreen(ChestMenu chestMenu, Inventory inventory, Component component) {
        super(chestMenu, inventory, component);
        this.containerRows = chestMenu.getRowCount();
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderType::guiTextured, CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, 17, 256, 256);
        for (int k = 0; k < this.containerRows; k++) {
            guiGraphics.blit(RenderType::guiTextured, CONTAINER_BACKGROUND, x, y + k * 18 + 17, 0, 17, this.imageWidth, 18, 256, 256);
        }
        guiGraphics.blit(RenderType::guiTextured, CONTAINER_BACKGROUND, x, y + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96, 256, 256);
    }


}