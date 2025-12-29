package yagen.waitmydawn.pack_up;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class LootStorageScreen extends AbstractContainerScreen<LootStorageMenu> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/shulker_box.png");

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
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                connection.send(new ServerboundCustomPayloadPacket(new PacketChangePage(-1)));
            }
        }).bounds(this.leftPos - 25, this.topPos, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                connection.send(new ServerboundCustomPayloadPacket(new PacketChangePage(1)));
            }
        }).bounds(this.leftPos + this.imageWidth + 5, this.topPos, 20, 20).build());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        String text = (this.menu.getCurrentPage() + 1) + " / " + Math.max(1, this.menu.getTotalPages());
        guiGraphics.drawString(this.font, text,
                (imageWidth - this.font.width(text)) / 2,
                -10, 0xFFFFFFFF, true);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos, topPos,
                0f, 0f,
                imageWidth, imageHeight,
                256, 256, -1
        );
    }
}
