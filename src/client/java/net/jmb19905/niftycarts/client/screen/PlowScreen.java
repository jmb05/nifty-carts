package net.jmb19905.niftycarts.client.screen;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.container.PlowMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class PlowScreen extends AbstractContainerScreen<PlowMenu> {
    private static final ResourceLocation PLOW_GUI_TEXTURES = new ResourceLocation(NiftyCarts.MOD_ID, "textures/gui/container/plow.png");

    public PlowScreen(final PlowMenu screenContainer, final Inventory inv, final Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderBg(final GuiGraphics guiGraphics, final float partialTicks, final int mouseX, final int mouseY) {
        final int i = (this.width - this.imageWidth) / 2;
        final int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(PLOW_GUI_TEXTURES, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}