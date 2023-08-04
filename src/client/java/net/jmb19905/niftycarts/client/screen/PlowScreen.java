package net.jmb19905.niftycarts.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.container.PlowMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class PlowScreen extends AbstractContainerScreen<PlowMenu> {
    private static final ResourceLocation PLOW_GUI_TEXTURES = new ResourceLocation(NiftyCarts.MOD_ID, "textures/gui/container/plow.png");

    public PlowScreen(final PlowMenu screenContainer, final Inventory inv, final Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderBg(final PoseStack stack, final float partialTicks, final int mouseX, final int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, PLOW_GUI_TEXTURES);
        final int i = (this.width - this.imageWidth) / 2;
        final int j = (this.height - this.imageHeight) / 2;
        blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }
}