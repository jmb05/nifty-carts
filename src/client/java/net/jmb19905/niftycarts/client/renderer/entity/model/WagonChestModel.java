package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.client.renderer.entity.WagonRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class WagonChestModel extends EntityModel<@NotNull WagonRenderState> {

    private final ModelPart chest0;
    private final ModelPart chest1;
    private final ModelPart chest2;

    public WagonChestModel(ModelPart root) {
        super(root);
        this.chest0 = root.getChild("chest0");
        this.chest1 = root.getChild("chest1");
        this.chest2 = root.getChild("chest2");
    }

    @Override
    public void setupAnim(@NotNull WagonRenderState state) {
        super.setupAnim(state);
        this.chest0.visible = false;
        this.chest1.visible = false;
        this.chest2.visible = false;
        if (state.chestCount > 0) {
            this.chest0.visible = true;
        }
        if (state.chestCount > 1) {
            this.chest1.visible = true;
        }
        if (state.chestCount > 2) {
            this.chest2.visible = true;
        }
    }
}
