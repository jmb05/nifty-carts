package net.jmb19905.niftycarts.client.screen;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.container.WagonMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class WagonScreen extends AbstractContainerScreen<@NotNull WagonMenu> {
    private static final Identifier CONTAINER_BACKGROUND = NiftyCarts.resLoc("textures/gui/container/wagon.png");
    private final int containerRows;

    public WagonScreen(WagonMenu chestMenu, Inventory inventory, Component component) {
        super(chestMenu, inventory, component);
        this.containerRows = chestMenu.getRowCount();
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
        if (this.containerRows >= 12) {
            this.imageWidth = this.imageWidth + 54;
            this.imageHeight = 114 + 9 * 18;
            this.inventoryLabelY = this.inventoryLabelY - 54;
            this.inventoryLabelX += 27;
        }
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float f, int i, int j) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (this.containerRows < 12) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                    x, y, 0, 0, this.imageWidth, 17, 256, 256);
            for (int k = 0; k < this.containerRows; k++) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                        x, y + k * 18 + 17, 0, 17, this.imageWidth, 18, 256, 256);
            }
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                    x, y + this.containerRows * 18 + 17, 0, 36, this.imageWidth, 96, 256, 256);
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                    x, y, 0, 132, 230, 17, 256, 256);
            for (int k = 0; k < 9; k++) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                        x, y + k * 18 + 17, 0, 17 + 132, 230, 18, 256, 256);
            }
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                    x, y + 9 * 18 + 17, 0, 167, 230, 17, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                    x + 27, y + 9 * 18 + 17 + 13, 0, 49, this.imageWidth, 82, 256, 256);
        }
    }


}