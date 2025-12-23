package yagen.waitmydawn;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class LootStorageScreen extends AbstractContainerScreen<LootStorageMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/container/shulker_box.png");

    public LootStorageScreen(LootStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            PacketDistributor.sendToServer(new PacketChangePage(-1));
        }).bounds(this.leftPos - 25, this.topPos, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            PacketDistributor.sendToServer(new PacketChangePage(1));
        }).bounds(this.leftPos + this.imageWidth + 5, this.topPos, 20, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        String text = (this.menu.getCurrentPage() + 1) + " / " + Math.max(1, this.menu.getTotalPages());
        guiGraphics.drawString(this.font, text,
                this.leftPos + (imageWidth - this.font.width(text)) / 2,
                this.topPos - 10, 0xFFFFFF, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
