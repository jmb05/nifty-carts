package net.jmb19905.niftycarts.client.screen;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.container.SeedDrillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SeedDrillScreen extends AbstractContainerScreen<SeedDrillMenu> {
    private static final ResourceLocation SEED_DRILL_GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/gui/container/seed_drill.png");

    public SeedDrillScreen(SeedDrillMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void renderBg(final GuiGraphics guiGraphics, final float partialTicks, final int mouseX, final int mouseY) {
        final int i = (this.width - this.imageWidth) / 2;
        final int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(SEED_DRILL_GUI_TEXTURES, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
